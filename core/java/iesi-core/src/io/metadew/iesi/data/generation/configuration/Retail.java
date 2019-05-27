package io.metadew.iesi.data.generation.configuration;

import io.metadew.iesi.data.generation.execution.GenerationComponentExecution;
import io.metadew.iesi.data.generation.execution.GenerationDataExecution;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Retail extends GenerationComponentExecution {

    public Retail(GenerationDataExecution execution) {
        super(execution);
    }

    public String color() {
        return call("color.colorName");
    }

    public String department() {
        return department(3);
    }

    public String department(int max) {
        return department(max, false);
    }

    public String department(int max, boolean fixedAmount) {
        int num;

        if (fixedAmount) {
            num = max;
        } else {
            num = 1 + this.getGenerationTools().getRandomTools().number(max);
        }

        List<String> categories = getCategories(num);

        if (num > 1) {
            return mergeCategories(categories);
        } else {
            return categories.get(0);
        }
    }

    public String productName() {
        return fetch("retail.product_name.adjective")
                + " " + fetch("retail.product_name.material")
                + " " + fetch("retail.product_name.product");
    }

    public String material() {
        return fetch("retail.product_name.material");
    }

    public BigDecimal price() {
        return price(0, 100);
    }

    public BigDecimal price(int min, int max) {
        return new BigDecimal(this.getGenerationTools().getRandomTools().range(min, max))
                .round(new MathContext(2, RoundingMode.HALF_UP));
    }

    public String promotionCode() {
        return promotionCode(6);
    }

    public String promotionCode(int digits) {
        return fetch("retail.promotion_code.adjective")
                + fetch("retail.promotion_code.noun")
                + getComponent(Number.class).number(digits);
    }

    // Helpers

    private List<String> getCategories(int num) {
        List<String> categories = new ArrayList<>(num);

        while (categories.size() != num) {
            String category = fetch("retail.department");
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }

        return categories;
    }

    private String mergeCategories(List<String> categories) {
        List<String> commaCategories = categories.subList(0, categories.size() - 1);
        String commaSeparated = this.getGenerationTools().getStringTools().join(commaCategories, ", ");
        return commaSeparated + getSeparator() + categories.get(categories.size() - 1);
    }
}
