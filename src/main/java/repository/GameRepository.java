package com.example.gameflixauth.repository;

import com.example.gameflixauth.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}