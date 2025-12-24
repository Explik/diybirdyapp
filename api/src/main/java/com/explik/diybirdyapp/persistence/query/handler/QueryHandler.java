package com.explik.diybirdyapp.persistence.query.handler;

public interface QueryHandler<TQuery, TResult> {
    TResult handle(TQuery query);
}
