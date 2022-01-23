package com.heji.server.data.mongo.repository;

import com.heji.server.data.mongo.MBill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

//findAndModify 查询并修改
//upsert补增
public interface MBillRepository extends MongoRepository<MBill, String> {


    // 回傳id欄位值有包含在參數之中的文件數量
    //@Query(value = "{'_id': {'$in': ?0}}", count = true)
    //int countByIdIn(List<String> ids);
    List<MBill> findMBillsByBookId(String bookId);
    List<MBill> findMBillsByBookIdAndTimeBetween(String bookId,String startTime,String endTime);
}
