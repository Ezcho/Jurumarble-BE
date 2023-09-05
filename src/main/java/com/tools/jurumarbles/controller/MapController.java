package com.tools.jurumarbles.controller;

import com.tools.jurumarbles.model.MapEntity;
import com.tools.jurumarbles.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maps")
public class MapController {
    @Autowired
    private MapService service;

    @PostMapping
    public MapEntity create(@RequestBody MapEntity map) {
        return service.createMap(map);
    }

    @GetMapping
    public Iterable<MapEntity> read() {
        return service.getAllMaps();
    }

    @GetMapping("/{id}")
    public MapEntity read(@PathVariable int id) {
        return service.getMapById(id);
    }

    @PutMapping("/{id}")
    public MapEntity update(@PathVariable int id, @RequestBody MapEntity map) {
        return service.updateMap(id, map);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.deleteMap(id);
    }
}
