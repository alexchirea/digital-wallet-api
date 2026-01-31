package ro.lexera.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.lexera.wallet.model.entity.StatusRegistryEntity;

import java.util.UUID;

@Repository
public interface StatusRegistryRepository extends JpaRepository<StatusRegistryEntity, UUID> {
}
