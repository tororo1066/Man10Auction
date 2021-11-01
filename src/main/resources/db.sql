create table listing_data(

    id int unsigned auto_increment,
    auction_id int,
    genre int default 0,
    item longtext null,
    reserve_price int default 0,
    unit_price int default 1,
    suc_bid_amount int default 0,
    seller_name varchar(16) null,
    seller_uuid varchar(36) null,
    suc_bidder_name varchar(16) null,
    suc_bidder_uuid varchar(36) null,
    seller_custom_name varchar(36) null,
    item_status int default 0,
    money_status int default 0,
    register_date DATETIME null,
    start_date DATETIME null,
    end_date DATETIME null,
    receive_item_date DATETIME null,
    receive_money_date DATETIME null,

    primary key(id)
);

create table bidding_data(

    id int unsigned auto_increment,
    auction_id int,
    listing_id int,
    bidder_name varchar(16),
    bidder_uuid varchar(36),
    bidding_price int,
    money_status boolean default false,
    bidding_date DATETIME null,
    isSucceed boolean default false,

    primary key(id)
);

create table auction_data(

    id int unsigned auto_increment,
    auction_status int default 0,
    start_date DATETIME null,
    end_Date DATETIME null,

    primary key(id)

);