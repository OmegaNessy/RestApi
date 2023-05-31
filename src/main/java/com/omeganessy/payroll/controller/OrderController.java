package com.omeganessy.payroll.controller;

import com.omeganessy.payroll.entity.Order;
import com.omeganessy.payroll.entity.Status;
import com.omeganessy.payroll.exception.OrderNotFoundException;
import com.omeganessy.payroll.repository.OrderRepository;
import org.apache.coyote.Response;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {
    private OrderRepository orderRepository;
    private OrderModelAssembler assembler;

    OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.orderRepository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders/all")
    CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> entityModels = orderRepository.findAll().stream()
                .map(order -> assembler.toModel(order))
                .toList();
        return CollectionModel.of(entityModels,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    EntityModel<Order> one(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(order -> assembler.toModel(order))
                .orElseThrow(() -> new OrderNotFoundException(id));

    }

    @PostMapping("/orders")
    ResponseEntity<?> newOrder(@RequestBody Order newOrder) {
        newOrder.setStatus(Status.IN_PROGRESS);
        EntityModel<Order> entityModel = assembler.toModel(orderRepository.save(newOrder));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/orders/{id}")
    ResponseEntity<?> replaceOrder(@PathVariable Long id, @PathVariable Order newOrder) {
        Order oldOrder = orderRepository.findById(id).orElseGet(() -> {
            newOrder.setId(id);
            newOrder.setStatus(Status.IN_PROGRESS);
            return orderRepository.save(newOrder);
        });

        if (Status.IN_PROGRESS == oldOrder.getStatus()){
            oldOrder.setDescription(newOrder.getDescription());
            return ResponseEntity.ok(assembler.toModel(orderRepository.save(oldOrder)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("Order in status " + oldOrder.getStatus() + " can not be modified or replaced"));
    }

    @PutMapping("/orders/{id}/complete")
    ResponseEntity<?> complete(@PathVariable Long id){
        Order order = orderRepository.findById(id).orElseThrow(()-> new OrderNotFoundException(id));

        if (Status.IN_PROGRESS == order.getStatus()){
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("Can not complete order with status " + order.getStatus()));
    }

    @DeleteMapping("/orders/{id}")
    ResponseEntity<?> cancel(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (Status.IN_PROGRESS == order.getStatus()) {
            order.setStatus(Status.CANCELED);
            return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
        }

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("Order in status " + order.getStatus() + " can not be canceled"));
    }
}
