package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setUp() {

        orderController = new OrderController(null, null);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("Circle");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("An item that is circle in shape");
        List<Item> items = new ArrayList<Item>();
        items.add(item);


        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(2.99);
        cart.setTotal(total);
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(userRepository.findByUsername("someone")).thenReturn(null);
    }

    @Test
    public void testSubmitOrder() {
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertEquals(1, userOrder.getItems().size());

    }

    @Test
    public void testSubmitOrderUserNotFound() {
        ResponseEntity<UserOrder> response = orderController.submit("someone");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersForUser() {
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("test");
        assertNotNull(userOrders);
        assertEquals(200, userOrders.getStatusCodeValue());
        List<UserOrder> orders = userOrders.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testGetOrdersForUserNotFound() {
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("someone");
        assertNotNull(userOrders);
        assertEquals(404, userOrders.getStatusCodeValue());
    }
}
