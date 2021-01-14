package nikolaiev.v.o.shop.repos;

import nikolaiev.v.o.shop.domain.LinkedDirectory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkedDirectoryRepo extends JpaRepository<LinkedDirectory, Long> {
    LinkedDirectory findByDirectoryType (String directoryType);
}
