package com.increff.assure.service;

import com.increff.assure.dao.ProductMasterDao;
import com.increff.assure.pojo.ProductMasterPojo;
import model.data.ProductMasterData;
import model.form.ProductUpdateForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductMasterService extends AbstractService {
    @Autowired
    private ProductMasterDao productMasterDao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(ProductMasterPojo product) throws ApiException {
        checkIfProductExists(product.getClientId(), product.getClientSkuId());
        productMasterDao.insert(product);
    }

    private void checkIfProductExists(Long clientId, String clientSkuId) throws ApiException {
        ProductMasterPojo exists = productMasterDao.selectByClientIdAndClientSku(clientId, clientSkuId);
        checkNull(exists, "Duplicate ClientSKUs present.");
    }

    public ProductMasterPojo getCheckId(Long id) throws ApiException {
        ProductMasterPojo product = productMasterDao.select(id);
        checkNotNull(product, "Product (ID:" + id + ") does not exist.");
        return product;
    }

    @Transactional(rollbackFor = ApiException.class)
    public void addList(List<ProductMasterPojo> productList) throws ApiException {
        for (ProductMasterPojo pojo : productList)
            add(pojo);
    }

    public List<ProductMasterPojo> getAll() {
        return productMasterDao.selectAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Long clientId, String clientSkuId, ProductUpdateForm productForm) throws ApiException {
        ProductMasterPojo productMaster = productMasterDao.selectByClientIdAndClientSku(clientId, clientSkuId);
        copySourceToDestination(productMaster, productForm);
    }

    public Long getClientIdOfProduct(Long globalSkuId) throws ApiException {
        return getCheckId(globalSkuId).getClientId();
    }

    public ProductMasterPojo getByClientAndClientSku(Long clientId, String clientSkuId) throws ApiException {
        ProductMasterPojo exists = productMasterDao.selectByClientIdAndClientSku(clientId, clientSkuId);
        checkNotNull(exists, "Product with ClientID:"+clientId+" ClientSKUId:"+clientSkuId+" doesn't exist.");
        return exists;
    }

    public List<ProductMasterPojo> getByClientId(Long clientId) {
        return productMasterDao.selectByClientId(clientId);
    }
}
