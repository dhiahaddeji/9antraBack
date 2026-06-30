package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Contact;
import com.esprit.springjwt.entity.Progress;
import com.esprit.springjwt.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    @Autowired
    ContactRepository ContactRepository;
    //add contact
    public Contact addContact(Contact Contact){ return ContactRepository.save(Contact);
    }


    }



