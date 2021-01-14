package nikolaiev.v.o.shop.repos;

import nikolaiev.v.o.shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
}
