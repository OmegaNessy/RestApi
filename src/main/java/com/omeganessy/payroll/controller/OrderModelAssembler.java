package com.omeganessy.payroll.controller;

import com.omeganessy.payroll.entity.Order;
import com.omeganessy.payroll.entity.Status;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class OrderModelAssembler implements RepresentationModelAssembler<Order, EntityModel<Order>> {
    @Override
    public EntityModel<Order> toModel(Order entity) {
        EntityModel<Order> entityModel = EntityModel.of(entity,
                linkTo(methodOn(OrderController.class).all()).withRel("orders"),
                linkTo(methodOn(OrderController.class).one(entity.getId())).withSelfRel());

        if(Status.IN_PROGRESS == entity.getStatus()){
            entityModel.add(
                    linkTo(methodOn(OrderController.class).cancel(entity.getId())).withRel("cancle"),
                    linkTo(methodOn(OrderController.class).complete(entity.getId())).withRel("complete")
            );
        }
        return entityModel;
    }
}
