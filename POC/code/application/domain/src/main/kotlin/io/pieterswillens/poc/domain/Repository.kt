package io.pieterswillens.poc.domain

interface Repository<ID, AGGREGATE> {
    fun save(aggregate: AGGREGATE)
    fun get(id : ID): AGGREGATE?
}