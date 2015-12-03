package pl.lodz.p.ftims.pai.repository;

import pl.lodz.p.ftims.pai.domain.Transporter;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Transporter entity.
 */
public interface TransporterRepository extends JpaRepository<Transporter,Long> {

}
