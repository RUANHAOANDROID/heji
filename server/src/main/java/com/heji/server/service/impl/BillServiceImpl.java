package com.heji.server.service.impl;

import com.heji.server.data.mongo.BaseMongoTemplate;
import com.heji.server.data.mongo.MBill;
import com.heji.server.data.mongo.repository.MBillRepository;
import com.heji.server.service.BillService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

@Slf4j
@Service("BillService")
public class BillServiceImpl extends BaseMongoTemplate implements BillService {
    private static final String BILL = "bill";
    private static final String BILL_IMAGE = "bill_image";
    final
    MBillRepository mBillRepository;

    public BillServiceImpl(MBillRepository mBillRepository) {
        this.mBillRepository = mBillRepository;
    }

    @Override
    public String addBill(MBill bill) {
        MBill saveBill = getMongoTemplate().save(bill, BILL);
        return saveBill.get_id();
    }

    @Override
    public List<String> addBills(List<MBill> bills) {
        List<MBill> saves = getMongoTemplate().insert(bills);
        List<String> ids = saves.stream().map(new Function<MBill, String>() {

            @Override
            public String apply(MBill mBill) {
                return mBill.get_id();
            }
        }).collect(Collectors.toList());
        return ids;
    }

    @Override
    public boolean removeBill(String _id) {
        Criteria cr = Criteria.where("_id").is(_id);
        Query query = Query.query(cr);
        DeleteResult updateResult = getMongoTemplate().remove(query, BILL);
        log.info("删除 {} 条, _id={}", updateResult.getDeletedCount(), _id);
        return updateResult.getDeletedCount() > 0;
    }

    @Override
    public MBill updateBill(MBill bill) {
//        Criteria cr = Criteria.where("_id").is(bill.get_id());
//        Query query = Query.query(cr);
//        Update update = new Update().set("images", bill.getImages());
//        // 执行更新，如果没有找到匹配查询的文档，则创建并插入一个新文档
//        UpdateResult updateResult = getMongoTemplate().upsert(query, update, MBill.class, BILL);
//        updateResult.getMatchedCount();
        mBillRepository.save(bill);
        return bill;
    }

    @Override
    public String upInstImages(String _id, String[] images) {
        Criteria cr = Criteria.where("_id").is(_id);
        Query query = Query.query(cr);
        Update update = new Update().set("images", images);
        // 执行更新，如果没有找到匹配查询的文档，则创建并插入一个新文档
        UpdateResult updateResult = getMongoTemplate().upsert(query, update, MBill.class, BILL);
        return _id;
    }

    @Override
    public String removeImage(String _id, String imageId) {
        Criteria cr = Criteria.where("_id").is(_id);
        Query query = Query.query(cr);
        Update update = new Update().pull("images", imageId);
        // 执行更新，如果没有找到匹配查询的文档，则创建并插入一个新文档
        UpdateResult updateResult = getMongoTemplate().updateFirst(query, update, MBill.class, BILL);
        return _id;
    }

    @Override
    public List<MBill> getBills(String book_id, String startDate, String endDate) {
        if (startDate.equals("0") || endDate.equals("0"))
            return mBillRepository.findMBillsByBookId(book_id);
        return mBillRepository.findMBillsByBookIdAndTimeBetween(book_id, startDate, endDate);
//        // 创建条件对象
//        Criteria criteria = Criteria.where("book_id").is(book_id)
//                .where("time").gte(startDate).lte(endDate);
//        // 创建查询对象，然后将条件对象添加到其中，然后根据指定字段进行排序
//        Query query = new Query(criteria).with(Sort.by("time"));
//        List<MBill> documentList = getMongoTemplate().find(query, MBill.class, BILL);
//        return documentList;
    }

    @Override
    public MBill getBillInfo(String billId) {
        Criteria cr = Criteria.where("_id").is(billId);
        Query query = Query.query(cr);
        MBill bill = getMongoTemplate().findOne(query, MBill.class, BILL);
        return bill;
//        MBill mBill = new MBill();
//        mBill.set_id(billId);
//        Optional<MBill> bill = mBillRepository.findOne(Example.of(mBill));
//        if (bill.isPresent())
//            return bill.get();
//        return null;
    }

    @Override
    public boolean exists(String _id) {
        Criteria cr = Criteria.where("_id").is(_id);
        Query query = new Query(cr);
        return getMongoTemplate().exists(query, BILL);
    }

    @Override
    public void sumBills() {

    }

    @Override
    public String sumBills(String year, String month) {
        Criteria criteria = Criteria.where("time").is(year);
        Aggregation aggregation = Aggregation
                .newAggregation(match(criteria), group("year", year).sum("money").as("total"));
        AggregationResults<String> groupResults = getMongoTemplate().aggregate(
                aggregation, BILL, String.class);
        List<String> salesReport = groupResults.getMappedResults();
        return "100";
    }

    @Override
    public void avgBills() {

    }

    @Override
    protected void init() {
        MongoTemplate mongoTemplate = getMongoTemplate();
        if (!mongoTemplate.collectionExists(BILL)) {
            mongoTemplate.createCollection(BILL);
            IndexOperations indexOpe = mongoTemplate.indexOps(BILL);
            indexOpe.ensureIndex(new CompoundIndexDefinition(new Document("_id", "hashed")));
        }
    }
}
