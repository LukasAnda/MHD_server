package sk.vava.mhd.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import sk.vava.mhd.model.Transport

interface TransportRepository: JpaRepository<Transport, Long>