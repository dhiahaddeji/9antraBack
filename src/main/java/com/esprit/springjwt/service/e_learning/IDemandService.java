package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Demand;
import com.esprit.springjwt.entity.e_learning.DemandCategory;

import java.util.List;

public interface IDemandService {
    Demand add(Demand demand, Long idPath);

    List<Demand> getAll();

    List<Demand> getByCategory(DemandCategory demandCategory);

    Demand getOne(Long idDemand);


    void changeToResponded(Demand d);
}
