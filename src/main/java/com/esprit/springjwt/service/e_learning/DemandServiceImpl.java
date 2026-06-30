package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Demand;
import com.esprit.springjwt.entity.e_learning.DemandCategory;
import com.esprit.springjwt.entity.e_learning.Path;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.repository.e_learning.IDemandRepository;
import com.esprit.springjwt.repository.e_learning.IPathRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandServiceImpl implements IDemandService{

    @Autowired
    IDemandRepository demandRepository;

    @Autowired
    IPathRepository pathRepository;


    @Override
    public Demand add(Demand demand, Long idPath) {

        demand.setResponded(false);

        if( idPath != null && demand.getCategory() == DemandCategory.Path){
            Path p = pathRepository.findById(idPath).orElseThrow(() -> new RecordNotFoundException("Path not found"));

            demand.setPath(p);
            return demandRepository.save(demand);
        }

        return demandRepository.save(demand);
    }

    @Override
    public List<Demand> getAll() {

        List<Demand> demands = demandRepository.findAll();
        if(demands.isEmpty())
            throw new RecordNotFoundException("No demand found");


        return demands;
    }

    @Override
    public List<Demand> getByCategory(DemandCategory demandCategory) {

        Demand demandExample = new Demand();
        demandExample.setCategory(demandCategory);

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withMatcher("category", ExampleMatcher.GenericPropertyMatcher::exact);

        Example<Demand> example = Example.of(demandExample, exampleMatcher);

        List<Demand> demands = demandRepository.findAll(example);
        if(demands.isEmpty())
            throw new RecordNotFoundException("No demand found");

        return demands;
    }

    @Override
    public Demand getOne(Long idDemand) {
        Demand d = demandRepository.findById(idDemand).orElseThrow(() -> new RecordNotFoundException("Demand not found"));
        return d;
    }

    @Override
    public void changeToResponded(Demand d) {
        d.setResponded(true);
        demandRepository.save(d);
    }
}
