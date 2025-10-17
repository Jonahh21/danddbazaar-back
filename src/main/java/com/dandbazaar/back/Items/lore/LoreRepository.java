package com.dandbazaar.back.Items.lore;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dandbazaar.back.common.reporegister.Registered;

@Registered(Lore.class)
public interface LoreRepository extends JpaRepository<Lore, Long> {

}
