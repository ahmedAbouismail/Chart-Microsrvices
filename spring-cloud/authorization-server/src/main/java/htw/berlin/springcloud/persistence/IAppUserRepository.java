package htw.berlin.springcloud.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IAppUserRepository extends CrudRepository<AppUserEntity, Integer> {

//    @Transactional
    AppUserEntity findByEmail(String email);
}
