package com.dandbazaar.back.Items;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dandbazaar.back.common.reporegister.Registered;

@Registered(Item.class)
public interface ItemRepository extends JpaRepository<Item, Long> {

}
