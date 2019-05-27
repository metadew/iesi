package io.metadew.iesi.cockpit.backend;

import java.io.Serializable;
import java.util.Collection;

import io.metadew.iesi.cockpit.backend.data.Category;
import io.metadew.iesi.cockpit.backend.data.Product;
import io.metadew.iesi.cockpit.backend.mock.MockDataService;

/**
 * Back-end service interface for retrieving and updating product data.
 */
public abstract class DataService implements Serializable {

    public abstract Collection<Product> getAllProducts();

    public abstract Collection<Category> getAllCategories();

    public abstract void updateProduct(Product p);

    public abstract void deleteProduct(int productId);

    public abstract Product getProductById(int productId);

    public abstract void updateCategory(Category category);

    public abstract void deleteCategory(int categoryId);

    public static DataService get() {
        return MockDataService.getInstance();
    }

}
