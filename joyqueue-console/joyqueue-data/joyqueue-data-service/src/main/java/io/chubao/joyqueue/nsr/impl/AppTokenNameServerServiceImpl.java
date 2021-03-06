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
package io.chubao.joyqueue.nsr.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.chubao.joyqueue.domain.AppToken;
import io.chubao.joyqueue.model.PageResult;
import io.chubao.joyqueue.model.QPageQuery;
import io.chubao.joyqueue.convert.NsrAppTokenConverter;
import io.chubao.joyqueue.model.domain.ApplicationToken;
import io.chubao.joyqueue.model.domain.OperLog;
import io.chubao.joyqueue.model.query.QApplicationToken;
import io.chubao.joyqueue.nsr.model.AppTokenQuery;
import io.chubao.joyqueue.nsr.AppTokenNameServerService;
import io.chubao.joyqueue.nsr.NameServerBase;
import io.chubao.joyqueue.toolkit.time.SystemClock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wangxiaofei1 on 2019/1/2.
 */
@Service("appTokenNameServerService")
public class AppTokenNameServerServiceImpl extends NameServerBase implements AppTokenNameServerService {

    public static final String ADD_TOKEN="/apptoken/add";
    public static final String UPDATE_TOKEN="/apptoken/update";
    public static final String REMOVE_TOKEN="/apptoken/remove";
    public static final String GETBYID_TOKEN="/apptoken/getById";
    public static final String FINDBYQUERY_TOKEN="/apptoken/findByQuery";
    public static final String LIST_TOKEN="/apptoken/list";

    private NsrAppTokenConverter nsrAppTokenConverter = new NsrAppTokenConverter();

    @Override
    public ApplicationToken findById(Long  id) throws Exception {
        String result = post(GETBYID_TOKEN,Long.valueOf(id));
        AppToken appToken = JSON.parseObject(result,AppToken.class);
        return nsrAppTokenConverter.convert(appToken);
    }

    @Override
    public int add(ApplicationToken applicationToken) throws Exception {
        Long id = Long.valueOf(String.valueOf(applicationToken.getApplication().getId())+ String.valueOf(SystemClock.now()/1000));
        applicationToken.setId(id);
        AppToken appToken = nsrAppTokenConverter.revert(applicationToken);
        String result = postWithLog(ADD_TOKEN, appToken,OperLog.Type.APP_TOKEN.value(),OperLog.OperType.ADD.value(),appToken.getApp());
        return isSuccess(result);
    }

    @Override
    public int update(ApplicationToken token) throws Exception {
        String result = post(GETBYID_TOKEN,token.getId());
        AppToken nsrToken = JSON.parseObject(result, AppToken.class);

        if (nsrToken == null) {
            nsrToken = new AppToken();
        }
        nsrToken.setId(token.getId());
        if (token.getApplication().getCode() != null) {
            nsrToken.setApp(token.getApplication().getCode());
        }
        if (token.getToken() != null) {
            nsrToken.setToken(token.getToken());
        }
        if (token.getEffectiveTime() != null) {
            nsrToken.setEffectiveTime(token.getEffectiveTime());
        }
        if (token.getExpirationTime() != null) {
            nsrToken.setExpirationTime(token.getExpirationTime());
        }
        String success =  postWithLog(UPDATE_TOKEN, nsrToken,OperLog.Type.APP_TOKEN.value(),OperLog.OperType.UPDATE.value(),nsrToken.getApp());
        return isSuccess(success);
    }

    @Override
    public int delete(ApplicationToken applicationToken) throws Exception {
        AppToken nsrToken = nsrAppTokenConverter.revert(applicationToken);
        String result = postWithLog(REMOVE_TOKEN, nsrToken,OperLog.Type.APP_TOKEN.value(),OperLog.OperType.DELETE.value(),nsrToken.getApp());
        return isSuccess(result);
    }

    @Override
    public List<ApplicationToken> findByQuery(QApplicationToken qApplicationToken) throws Exception {
        AppTokenQuery appTokenQuery = new AppTokenQuery();
        if (qApplicationToken != null) {
            appTokenQuery.setApp(qApplicationToken.getApplication().getCode());
            appTokenQuery.setToken(qApplicationToken.getToken());
        }
        String result = post(LIST_TOKEN,appTokenQuery);
        List<AppToken> appTokenList = JSON.parseArray(result,AppToken.class);
        return appTokenList.stream().map(appToken -> nsrAppTokenConverter.convert(appToken)).collect(Collectors.toList());
    }


    @Override
    public PageResult<ApplicationToken> findByQuery(QPageQuery<QApplicationToken> query) throws Exception {
        QPageQuery<AppTokenQuery> pageQuery = new QPageQuery<>();
        pageQuery.setPagination(query.getPagination());
        AppTokenQuery appTokenQuery = new AppTokenQuery();
        if (query != null && query.getQuery() !=null && query.getQuery().getApplication() != null) {
            appTokenQuery.setApp(query.getQuery().getApplication().getCode());
            appTokenQuery.setToken(query.getQuery().getToken());
        }
        pageQuery.setQuery(appTokenQuery);
        String result = post(FINDBYQUERY_TOKEN,pageQuery);
        PageResult<AppToken> pageResult = JSON.parseObject(result,new TypeReference<PageResult<AppToken>>(){});

        PageResult<ApplicationToken> pageResult2 = new PageResult<>();
        pageResult2.setPagination(pageResult.getPagination());
        pageResult2.setResult(pageResult.getResult().stream().map(appToken -> nsrAppTokenConverter.convert(appToken)).collect(Collectors.toList()));
        return pageResult2;
    }

}
