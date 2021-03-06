/**
 * Copyright 2019 The JoyQueue Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chubao.joyqueue.service.impl;

import io.chubao.joyqueue.model.ListQuery;
import io.chubao.joyqueue.exception.ServiceException;
import io.chubao.joyqueue.exception.ValidationException;
import io.chubao.joyqueue.model.PageResult;
import io.chubao.joyqueue.model.domain.Application;
import io.chubao.joyqueue.model.domain.ApplicationToken;
import io.chubao.joyqueue.model.domain.Consumer;
import io.chubao.joyqueue.model.domain.Identity;
import io.chubao.joyqueue.model.domain.Producer;
import io.chubao.joyqueue.model.domain.TopicUnsubscribedApplication;
import io.chubao.joyqueue.model.domain.User;
import io.chubao.joyqueue.model.query.QApplication;
import io.chubao.joyqueue.model.QPageQuery;
import io.chubao.joyqueue.model.query.QApplicationToken;
import io.chubao.joyqueue.model.query.QConsumer;
import io.chubao.joyqueue.model.query.QProducer;
import io.chubao.joyqueue.nsr.AppTokenNameServerService;
import io.chubao.joyqueue.nsr.ConsumerNameServerService;
import io.chubao.joyqueue.nsr.ProducerNameServerService;
import io.chubao.joyqueue.repository.ApplicationRepository;
import io.chubao.joyqueue.service.ApplicationService;
import io.chubao.joyqueue.service.ApplicationUserService;
import io.chubao.joyqueue.service.UserService;
import com.google.common.base.Preconditions;
import io.chubao.joyqueue.util.NullUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static io.chubao.joyqueue.exception.ServiceException.BAD_REQUEST;
import static io.chubao.joyqueue.exception.ServiceException.INTERNAL_SERVER_ERROR;
import static io.chubao.joyqueue.exception.ValidationException.NOT_FOUND_EXCEPTION_STATUS;
import static io.chubao.joyqueue.exception.ValidationException.UNIQUE_EXCEPTION_STATUS;

/**
 * Created by yangyang115 on 18-7-27.
 */
@Service("applicationService")
public class ApplicationServiceImpl extends PageServiceSupport<Application, QApplication,ApplicationRepository> implements ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private ProducerNameServerService producerNameServerService;
    @Autowired
    private ConsumerNameServerService consumerNameServerService;
    @Autowired
    private ApplicationUserService applicationUserService;
    @Autowired
    private AppTokenNameServerService appTokenNameServerService;
    @Autowired
    private UserService userService;

    @Override
    public int add(Application app) {
        //Validate unique
        Application apps = findByCode(app.getCode());
        if (NullUtil.isNotEmpty(apps)) {
            throw new ValidationException(UNIQUE_EXCEPTION_STATUS, getUniqueExceptionMessage());
        }
        //fill owner_id
        if (app.getOwner().getId() == null && app.getOwner().getCode() != null) {
            User user = userService.findByCode(app.getOwner().getCode());
            if (user != null) {
                app.getOwner().setId(user.getId());
            } else {
                throw new ValidationException(NOT_FOUND_EXCEPTION_STATUS, "owner|不存在");
            }
        }
        //Add
        return super.add(app);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int delete(final Application app) {
        try {
            //validate topic related producers and consumers
            Preconditions.checkArgument(NullUtil.isEmpty(producerNameServerService.findByQuery(new QProducer(new Identity(app.getId(), app.getCode())))),
                    String.format("app %s exists related producers", app.getCode()));
            Preconditions.checkArgument(NullUtil.isEmpty(consumerNameServerService.findByQuery(new QConsumer(app.getCode()))),
                    String.format("app %s exists related consumers", app.getCode()));
            //delete related app users
            applicationUserService.deleteByAppId(app.getId());
            //delete related app tokens
            List<ApplicationToken> tokens = appTokenNameServerService.findByQuery(new QApplicationToken(app.getCode()));
            if (NullUtil.isNotEmpty(tokens)) {
                for (ApplicationToken t : tokens) {
                    appTokenNameServerService.delete(t);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            String msg = "delete application error. ";
            logger.error(msg, e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, msg, e);
        }
        //delete app
        return super.delete(app);
    }

    @Override
    public Application findByCode(final String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return repository.findByCode(code);
    }

    @Override
    public PageResult<Application> findSubscribedByQuery(QPageQuery<QApplication> query) {
        if (query == null || query.getQuery() == null) {
            return PageResult.empty();
        }
        ListQuery<QApplication> listQuery = new ListQuery<>();
        listQuery.setQuery(query.getQuery());
        List<Application> applicationList = findByQuery(listQuery);
//        List<String> appList = getSubscribeList(query.getQuery());
//        if (applicationList != null && appList != null) {
//            applicationList = applicationList.stream().filter(application -> appList.contains(application.getCode())).collect(Collectors.toList());
//        }
        return new PageResult<>(query.getPagination(),applicationList);
    }
    //

    @Override
    public PageResult<TopicUnsubscribedApplication> findTopicUnsubscribedByQuery(QPageQuery<QApplication> query) {
        if (query == null || query.getQuery() == null) {
            return PageResult.empty();
        }
        if (query.getQuery() == null || query.getQuery().getSubscribeType() == null || query.getQuery().getTopic() == null
                || query.getQuery().getTopic().getCode() == null) {
            throw new ServiceException(BAD_REQUEST, "bad QApplication query argument.");
        }
        query.getQuery().setNoInCodes(getSubscribeList(query.getQuery()));
        PageResult<Application> applicationPageResult = repository.findUnsubscribedByQuery(query);
        if (NullUtil.isEmpty(applicationPageResult.getResult())) {
            return PageResult.empty();
        }
        return new PageResult(applicationPageResult.getPagination(), applicationPageResult.getResult().stream().map(app -> {
            TopicUnsubscribedApplication topicUnsubscribedApp = new TopicUnsubscribedApplication(app);
            topicUnsubscribedApp.setTopicCode(query.getQuery().getTopic().getCode());
            topicUnsubscribedApp.setSubscribeType(query.getQuery().getSubscribeType());
            if (query.getQuery().getSubscribeType() == Consumer.CONSUMER_TYPE) {
                //find consumer list by topic and app refer, then set showDefaultSubscribeGroup property
                QConsumer qConsumer = new QConsumer();
                qConsumer.setTopic(query.getQuery().getTopic());
                qConsumer.setNamespace(query.getQuery().getTopic().getNamespace().getCode());
                qConsumer.setReferer(app.getCode());
                try {
                    List<Consumer> consumers = consumerNameServerService.findByQuery(qConsumer);
                    topicUnsubscribedApp.setSubscribeGroupExist((consumers == null || consumers.size() < 1) ? false : true);
                } catch (Exception e) {
                    logger.error("can not find consumer list by topic and app refer.", e);
                    topicUnsubscribedApp.setSubscribeGroupExist(true);
                }
            }
            return topicUnsubscribedApp;
        }).collect(Collectors.toList()));
    }

    private List<String> getSubscribeList(QApplication query){
        try{
            List<String> noInCodes = null;
            if (query.getSubscribeType() != null) {
                if (query.getSubscribeType() == Producer.PRODUCER_TYPE) {
                    QProducer qProducer = new QProducer();

                    if (query.getTopic() == null ) {
                        throw new RuntimeException("topic is null");
                    }
                    qProducer.setTopic(query.getTopic());
                    List<Producer> producerList = producerNameServerService.findByQuery(qProducer);
                    if (producerList == null)return null;
                    noInCodes = producerList.stream().map(producer -> producer.getApp().getCode()).collect(Collectors.toList());
                }
//                if (query.getSubscribeType() == Consumer.CONSUMER_TYPE) {
////                    QConsumer qConsumer = new QConsumer();
////                    qConsumer.setTopic(query.getTopic());
////                    List<Consumer> consumerList = consumerNameServerService.findByQuery(qConsumer);
////                    if (consumerList == null)return null;
////                    noInCodes = consumerList.stream().map(producer -> producer.getApp().getCode()).collect(Collectors.toList());
////                }
            }
            return noInCodes;
        } catch (Exception e) {
            logger.error("getNoInCodes",e);
        }
        return null;
    }

    @Override
    public List<Application> findByCodes(List<String> codes) {
        return  repository.findByCodes(codes);
    }
}
