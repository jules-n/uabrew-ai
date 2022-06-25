package com.julesn.uabrewai.services.preferences;

import com.julesn.uabrewai.dto.Component;
import com.julesn.uabrewai.dto.Menu;
import com.julesn.uabrewai.services.rest.RestExchanger;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class PreferencesServiceImpl implements PreferencesService {

    @Setter(onMethod_ = {@Autowired})
    private RestExchanger restExchanger;

    @Setter(onMethod_ = {@Value("${uabrew.warehouse.host}")})
    private String warehouseHost;

    @Setter(onMethod_ = {@Value("${uabrew.warehouse.port}")})
    private String warehousePort;

    @Setter(onMethod_ = {@Value("${uabrew.bar.host}")})
    private String barHost;

    @Setter(onMethod_ = {@Value("${uabrew.bar.port}")})
    private String barPort;

    @Override
    public Set<String> search(String bar, String client) {
        Set<String> pref = new HashSet<>();
        Set<String> orders = restExchanger.fetch(barHost, barPort, Set.class, List.of("orders", bar, client), null);
        var menu = restExchanger.fetch(warehouseHost, barHost, Menu.class, List.of("menu", bar, "get"), null);
        var positions = orders.stream()
                .map( order -> menu.getPositions().stream().filter(menuPosition -> menuPosition.getName() == order).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> components = new HashSet<>();
        positions.stream().forEach(pos -> {
            pos.getComponents().stream().forEach(
                    component -> components.add(component.getName())
            );
        });
        components.stream().forEach( component -> {
        menu.getPositions().stream().forEach(
                pos -> {if (pos.getComponents().contains(component)) {pref.add(pos.getName());}}
                );}
        );
        Map<String, List<Double>> characteristicNames = new HashMap<>();
        positions.stream().forEach(pos -> pos.getCharacteristics().stream().forEach(
                characteristic -> {
                    try {
                        double val = NumberUtils.parseNumber(characteristic.getValue().toString(), Double.class);
                        if(characteristicNames.containsKey(characteristic.getName())) {
                            var list = characteristicNames.get(characteristic.getName());
                            list.add(val);
                            characteristicNames.put(characteristic.getName(), list);
                        } else {
                            characteristicNames.put(characteristic.getName(), List.of(val));
                        }
                    }catch (Exception e) {

                    }


                }
        ));
        Map<String, Double> characteristicNamesWithAvg = new HashMap<>();
        characteristicNames.forEach(
                (characteristicName, val) -> {
                    characteristicNamesWithAvg.put(characteristicName, val.stream().mapToDouble(a -> a).average().getAsDouble());
                }
        );
        menu.getPositions().forEach(
                position -> {
                    position.getCharacteristics().forEach(
                            characteristic -> {
                                if (characteristicNamesWithAvg.containsKey(characteristic.getName())) {
                                    double avgVal = characteristicNamesWithAvg.get(characteristic.getName());
                                    double val = NumberUtils.parseNumber(characteristic.getValue().toString(), Double.class);
                                    if ((avgVal+3) >= val && (avgVal-3)<=val) {
                                        pref.add(position.getName());
                                    }
                                }
                            }
                    );
                }
        );
        return pref;
    }
}
