![header](https://capsule-render.vercel.app/api?type=waving&height=300&color=gradient&text=Food%20Delivery)

- 내용 :  가게 사장님들과 고객들에게 편리한 배달 서비스를 제공하기 위한 배달 어플리케이션
- 한 줄 정리 : 배달 어플리케이션 아웃소싱 프로젝트

# 🚀 STACK 
**Environment**

![인텔리제이](   https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)
![깃허브](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![깃](https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white)
![POSTMAN](https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

**Development**

![자바](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SPRING BOOT](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![SQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

# 🤔 Authors
- [@fargoe](https://github.com/fargoe)
- [@yunseokim119](https://github.com/yunseokim119)
- [@banasu0723](https://github.com/banasu0723)
- [@tae98](https://www.github.com/tae98)

# 🙏 Acknowledgements

 - [Awesome Readme Templates](https://awesomeopensource.com/project/elangosundar/awesome-README-templates)
 - [Awesome README](https://github.com/matiassingers/awesome-readme)
 - [How to write a Good readme](https://bulldogjob.com/news/449-how-to-write-a-good-readme-for-your-github-project)

# 🖼️ Wireframe
![Screenshot 2024-09-24 at 4 44 54 PM](https://github.com/user-attachments/assets/a6459cdb-5667-4cbb-afde-1bb86a35b37f)

# 🔖 API Reference
## User
![Screenshot 2024-09-24 at 5 49 10 PM](https://github.com/user-attachments/assets/2021f6ff-4b74-4118-ada1-75a846c57d7e)

## Shop
![Screenshot 2024-09-24 at 5 51 26 PM](https://github.com/user-attachments/assets/2f6d760f-1e1c-4a7a-951c-a7128cd4c467)

![Screenshot 2024-09-24 at 5 51 33 PM](https://github.com/user-attachments/assets/a55575ba-15f2-4e8e-99aa-4ae41b7d3fca)

## Menu
![Screenshot 2024-09-24 at 5 52 52 PM](https://github.com/user-attachments/assets/a29eee5c-71c3-4154-8566-77704bca2770)

## Order
![Screenshot 2024-09-24 at 5 53 39 PM](https://github.com/user-attachments/assets/ba4ac06c-a15b-40ea-a0c9-8cc105e682b7)
![Screenshot 2024-09-24 at 5 54 01 PM](https://github.com/user-attachments/assets/01721673-844b-419e-a3b8-32363ba9529a)

## Review
![Screenshot 2024-09-24 at 5 54 57 PM](https://github.com/user-attachments/assets/4b3c4be7-a668-4e7b-a3b3-c5616825ee1b)
#  🧑‍💻 Test Coverage
![image (3)](https://github.com/user-attachments/assets/bda697ab-077a-4353-90f5-174ba999e2bc)


# ⚒️ ERD Diagram
![users](https://github.com/user-attachments/assets/7006c0c0-21fd-4463-bfae-687ec57cf519)


#  📊 SQL
      create table users
    (
        user_id     bigint auto_increment
            primary key,
        created_at  datetime(6)            null,
        modified_at datetime(6)            null,
        email       varchar(255)           null,
        password    varchar(255)           not null,
        role        enum ('OWNER', 'USER') not null,
        deleted     bit                    not null comment 'Soft-delete indicator'
    );
    
    create table shop
    (
        shop_id          bigint auto_increment
            primary key,
        created_at       datetime(6)    null,
        modified_at      datetime(6)    null,
        closed           bit            not null,
        closetime        time(6)        null,
        min_order_amount decimal(38, 2) not null,
        name             varchar(255)   null,
        opentime         time(6)        null,
        owner_id         bigint         not null,
        constraint FKea1di7i3b50tpkwrfkincd34g
            foreign key (owner_id) references users (user_id)
    );
    
    create table menu
    (
        id          bigint auto_increment
            primary key,
        created_at  datetime(6)    not null,
        menu_name   varchar(255)   not null,
        modified_at datetime(6)    not null,
        price       decimal(10, 2) not null,
        shop_id     bigint         null,
        status      varchar(50)    null,
        constraint FK15isgm71fu9ptldp68fa4xa5y
            foreign key (shop_id) references shop (shop_id)
    );
    
    create table orders
    (
        id           bigint auto_increment
            primary key,
        created_at   datetime(6)                                                          null,
        modified_at  datetime(6)                                                          null,
        address      varchar(255)                                                         null,
        menu_name    varchar(255)                                                         null,
        menu_price   double                                                               not null,
        order_status enum ('ACCEPTED', 'CANCELED', 'COMPLETED', 'IN_PROGRESS', 'PENDING') null,
        phone_number varchar(255)                                                         null,
        user_id      bigint                                                               null,
        menu_id      bigint                                                               null,
        shop_id      bigint                                                               not null,
        constraint FK1nojj2acwdssvxe1dnrkrmmed
            foreign key (menu_id) references menu (id),
        constraint FKqn03kko0738sehaal2gr2uxl6
            foreign key (shop_id) references shop (shop_id)
    );
    
    create table reviews
    (
        id             bigint auto_increment
            primary key,
        created_at     datetime(6)  null,
        modified_at    datetime(6)  null,
        rating         int          not null,
        review_content varchar(255) null,
        review_time    datetime(6)  null,
        shop_id        bigint       null,
        user_id        bigint       null,
        order_id       bigint       not null,
        constraint FKqwgq1lxgahsxdspnwqfac6sv6
            foreign key (order_id) references orders (id)
    );


