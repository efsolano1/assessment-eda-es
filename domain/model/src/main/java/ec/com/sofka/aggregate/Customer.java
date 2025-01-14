package ec.com.sofka.aggregate;

import ec.com.sofka.account.Account;
import ec.com.sofka.account.values.AccountId;
import ec.com.sofka.aggregate.events.AccountCreated;
import ec.com.sofka.aggregate.events.AccountUpdated;
import ec.com.sofka.aggregate.values.CustomerId;
import ec.com.sofka.generics.domain.DomainEvent;
import ec.com.sofka.generics.utils.AggregateRoot;

import java.math.BigDecimal;
import java.util.List;

//2. Creation of the aggregate class - The communication between the entities and the external world.
public class Customer extends AggregateRoot<CustomerId> {
    //5. Add the Account to the aggregate: Can't be final bc the aggregate is mutable by EventDomains
    private Account account;

    //To create the Aggregate the first time, ofc have to set the id as well.
    //Se llama cuando crear un cliente nuevo
    public Customer() {
        super(new CustomerId());
        //Add the handler to the aggregate
        setSubscription(new CustomerHandler(this));
    }

    //To rebuild the aggregate
    // Se utiliza reconstruir el estado de un cliente desde una fuente externa, como una base de datos o un sistema de eventos.
    private Customer(final String id) {
        super(CustomerId.of(id));
        //Add the handler to the aggregate
        setSubscription(new CustomerHandler(this));
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    //Remember that User as Aggregate is the open door to interact with the entities// Genera un evento de dominio (AccountCreated)
    public void createAccount(BigDecimal accountBalance, String accountNumber, String name, String userId , String status) {
        //Add the event to the aggregate
        addEvent(new AccountCreated(new AccountId().getValue(), accountNumber, accountBalance, name, userId,status)).apply();
    }


    //Remember that User as Aggregate is the open door to interact with the entities
    public void updateAccount(String accountId, BigDecimal accountBalance, String accountNumber, String name, String status , String  userId) {
        //Add the event to the aggregate
        addEvent(new AccountUpdated(accountId, accountBalance, accountNumber, name, status, userId)).apply();
    }

    //To rebuild the aggregate // Sirve para reconstruir el estado del agregado desde el historial de eventos.
    public static Customer from(final String id, List<DomainEvent> events) {
        Customer customer = new Customer(id);
        System.out.println("Informacion de  customer  "+customer.getAccount());
       // events.forEach((event) -> customer.addEvent(event).apply());
       // return customer;
        events.stream()
                .filter(event -> id.equals(event.getAggregateRootId()))
                .forEach((event) -> customer.addEvent(event).apply());
        return customer;
    }


}
