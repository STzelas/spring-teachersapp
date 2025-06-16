package gr.aueb.cf.schoolapp.core.filters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class Paginated<T> {

    List<T> data;
    long totalElements;
    int totalPages;
    int numberOfElements;
    int currentPage;
    int pageSize;

    public Paginated(Page<T> page) {
        this.data = page.getContent(); // getter του page
        this.totalElements = page.getTotalElements(); // getter του page
        this.totalPages = page.getTotalPages(); // getter του page
        this.numberOfElements = page.getNumberOfElements(); // getter του page
        this.currentPage = page.getNumber(); // getter του page
        this.pageSize = page.getSize(); // getter του page
    }
}
