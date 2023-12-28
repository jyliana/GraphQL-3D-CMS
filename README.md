# 3D-details content management system

## Overview

The process involves users conceptualizing and designing intricate 3D models, subsequently placing orders for the printing of specific details.
These orders are then taken for execution by various workers.

Users in the system have distinct roles: 
 - administrators
 - customers who initiate 3D model creation for printing
 - workers tasked with executing print orders. 


## Workflow

**Model Creation:**

Customers create detailed 3D models within the system. These models serve as the blueprint for the printing process.

**Order Placement:**

Upon finalizing their designs, customers place orders for the printing of specific details. These orders enter the system, awaiting assignment to workers.

**Order Execution:**

Workers take on the responsibility of executing orders. Each worker may take specific components of larger orders.

> customer
> - order
>   - model
>    - name   
>    - description
>    - type
>    - settings
>    - material
>  - quantity
>  - tradeDate
>  - dueDate
>  - deliveryAddress
>  - status

> execution
>  - worker
>    - orderId
>    - modelId
>    - partial quantity   
>    - total
>    - completed
>    - status   
>    - progress in %
>
> 
>  - worker
>    - orderId
>    - modelId
>    - partial quantity
>    - total
>    - completed
>    - status
>    - progress in %


## Requirements

Java 17  
PostgreSQL 16.1  
Spring Boot 3.2.0  
Netflix GraphQL DGS  
Maven

## DGS framework libraries
Here I use the powerful and succinct DGS framework, crafted by Netflix
to simplify the development and deployment of GraphQL services. It enhances the GraphQL experience, providing robust
tooling and utilities for building scalable and efficient APIs. 

These libraries collectively contribute to enhancing the capabilities of the DGS framework,
making it easier for developers to build robust and feature-rich GraphQL APIs in a Spring Boot environment.
Each library addresses specific aspects such as validation, integration, scalar types, real-time updates,
and efficient pagination. Depending on your project requirements, you can leverage these libraries to extend
and customize your GraphQL implementation with DGS.

**graphql-dgs-spring-boot-starter:**

The Spring Boot Starter for DGS simplifies the integration of the DGS framework with Spring Boot applications. 
It streamlines the setup process, making it easier to build GraphQL APIs using DGS within a Spring Boot project.

**graphql-dgs-extended-validation:**

This library extends the validation capabilities of the DGS framework. 
It provides additional validation features for your GraphQL queries, mutations, and types, ensuring that input 
data adheres to specified rules and constraints.


**graphql-dgs-extended-scalars:**

Extended Scalars enrich the set of scalar types supported by GraphQL. 
This library introduces additional scalar types beyond the standard ones, enhancing the flexibility of your GraphQL schemas.

**graphql-dgs-subscriptions-websockets-autoconfigure:**

Subscriptions in GraphQL enable real-time communication between the server and clients. 
This library adds support for handling GraphQL subscriptions over WebSockets in a DGS-powered application. 
It simplifies the setup for incorporating real-time updates into your GraphQL APIs.

**graphql-dgs-pagination:**

Pagination is a crucial aspect of managing large datasets in GraphQL. 
This library provides utilities and conventions for implementing pagination in your DGS-based GraphQL APIs. 
It assists in efficiently handling queries that involve fetching paginated data.

And last but not the least:

**GraphQL Code Generator**

Maven port of the Netflix DGS GraphQL Codegen gradle build plugin. 
This plugin automates the generation of Java classes based on your GraphQL schema. 
It analyzes the schema and produces corresponding Java types, queries, and mutations, 
eliminating the need for manual coding.


## Example of mutation request for GraphQL:

**Create order:**

    { 
      mutation orderCreate($order: OrderCreateInput!) 
        {
          orderCreate(order: $order) {
             id
             tradeDateTime
             dueDateTime
             status
             deliveryAddress
             orderDetails {
                id
                totalAmount
                availableAmount
                status
            }
        }
    }

**Variables for mutation:**

    {
        "order":{
        "userId": "bf1549a9-dff1-41d2-95f6-579489c04b97",
        "dueDate": "2023-12-29T12:01:38.264+02:00",
        "deliveryAddress": "London, Garden str. 10",
        "details":[
            { 
              "id": "f6fe7870-c487-4ee3-9eeb-127ac0be708d",
              "amount": 32
            },
            { 
              "id": "a952b467-ca66-4e09-9460-688ec469faa8",
              "amount": 52
            }
          ]
        }
    }
 

