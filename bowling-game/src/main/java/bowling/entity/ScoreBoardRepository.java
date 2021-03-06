package bowling.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreBoardRepository extends JpaRepository<ScoreBoard, Integer>{

}
