package com.julesn.uabrewai.controllers;

import com.julesn.uabrewai.services.preferences.PreferencesService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@CrossOrigin
public class PreferencesController {

    @Setter(onMethod_ = {@Autowired})
    private PreferencesService preferencesService;

    @GetMapping("{bar}/{client}")
    public ResponseEntity<Set<String>> getPreferences(@PathVariable("bar") String bar, @PathVariable("client") String client) {
        Set<String> pref = preferencesService.search(bar, client);
        return pref.isEmpty() ? ResponseEntity.badRequest().build() : ResponseEntity.ok(pref);
    }
}
