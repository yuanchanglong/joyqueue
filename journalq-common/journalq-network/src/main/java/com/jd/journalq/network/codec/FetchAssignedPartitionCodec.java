package com.jd.journalq.network.codec;

import com.google.common.collect.Lists;
import com.jd.journalq.network.command.FetchAssignedPartitionRequest;
import com.jd.journalq.network.command.FetchAssignedPartitionData;
import com.jd.journalq.network.command.JMQCommandType;
import com.jd.journalq.network.serializer.Serializer;
import com.jd.journalq.network.transport.codec.JMQHeader;
import com.jd.journalq.network.transport.codec.PayloadCodec;
import com.jd.journalq.network.transport.command.Type;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * FetchAssignedPartitionCodec
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2018/12/4
 */
public class FetchAssignedPartitionCodec implements PayloadCodec<JMQHeader, FetchAssignedPartitionRequest>, Type {

    @Override
    public FetchAssignedPartitionRequest decode(JMQHeader header, ByteBuf buffer) throws Exception {
        short dataSize = buffer.readShort();
        List<FetchAssignedPartitionData> data = Lists.newArrayListWithCapacity(dataSize);
        for (int i = 0; i < dataSize; i++) {
            FetchAssignedPartitionData fetchAssignedPartitionData = new FetchAssignedPartitionData();
            fetchAssignedPartitionData.setTopic(Serializer.readString(buffer, Serializer.SHORT_SIZE));
            fetchAssignedPartitionData.setSessionTimeout(buffer.readInt());
            fetchAssignedPartitionData.setNearby(buffer.readBoolean());
            data.add(fetchAssignedPartitionData);
        }

        FetchAssignedPartitionRequest fetchAssignedPartitionRequest = new FetchAssignedPartitionRequest();
        fetchAssignedPartitionRequest.setApp(Serializer.readString(buffer, Serializer.SHORT_SIZE));
        fetchAssignedPartitionRequest.setData(data);
        return fetchAssignedPartitionRequest;
    }

    @Override
    public void encode(FetchAssignedPartitionRequest payload, ByteBuf buffer) throws Exception {
        buffer.writeShort(payload.getData().size());
        for (FetchAssignedPartitionData fetchAssignedPartitionData : payload.getData()) {
            Serializer.write(fetchAssignedPartitionData.getTopic(), buffer, Serializer.SHORT_SIZE);
            buffer.writeInt(fetchAssignedPartitionData.getSessionTimeout());
            buffer.writeBoolean(fetchAssignedPartitionData.isNearby());
        }
        Serializer.write(payload.getApp(), buffer, Serializer.SHORT_SIZE);
    }

    @Override
    public int type() {
        return JMQCommandType.FETCH_ASSIGNED_PARTITION_REQUEST.getCode();
    }
}