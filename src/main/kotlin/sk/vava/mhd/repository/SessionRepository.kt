package sk.vava.mhd.repository

import org.springframework.data.jpa.repository.JpaRepository
import sk.vava.mhd.model.Session
import sk.vava.mhd.model.Transport

interface SessionRepository: JpaRepository<Session, Long>