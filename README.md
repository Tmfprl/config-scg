add user infomatiotable

create table srlk.gyeun_user_info
(
    user_no       bigserial      not null,
    user_password varchar(32) not null,
    user_name varchar(50) default NULL::character varying,
    email         varchar(100) ,
    creates_date  timestamp,
    last_modified_by varchar(100) not null,
    user_id       varchar(32) not null,
    constraint gyeun_user_info_pk PRIMARY KEY (user_no)

);

refernaces table 

-- auto-generated definition
create table dwu_user
(
    user_no            bigserial
        constraint pk_dwu_user
            primary key,
    created_date       timestamp,
    modified_date      timestamp,
    created_by         varchar(255) default NULL::character varying,
    last_modified_by   varchar(255) default NULL::character varying,
    email_addr         varchar(100)                                 not null,
    encrypted_password varchar(100) default NULL::character varying,
    role_id            varchar(20)                                  not null,
    user_id            varchar(255)                                 not null
        constraint uk_dwu_user_01
            unique,
    user_name          varchar(50)                                  not null,
    refresh_token      varchar(255) default NULL::character varying,
    user_state_code    varchar(20)  default '00'::character varying not null,
    last_login_date    timestamp,
    login_fail_count   smallint     default 0                       not null,
    kakao_id           varchar(100),
    naver_id           varchar(100)
);

comment on table dwu_user is '사용자';

comment on column dwu_user.user_no is '사용자 번호';

comment on column dwu_user.created_date is '생성 일시';

comment on column dwu_user.modified_date is '수정 일시';

comment on column dwu_user.created_by is '생성자 ID';

comment on column dwu_user.last_modified_by is '수정자 ID';

comment on column dwu_user.email_addr is '이메일 주소';

comment on column dwu_user.encrypted_password is '암호화된 비밀번호';

comment on column dwu_user.role_id is '권한 ID';

comment on column dwu_user.user_id is '사용자 ID';

comment on column dwu_user.user_name is '사용자 이름';

comment on column dwu_user.refresh_token is 'refresh token';

comment on column dwu_user.user_state_code is '회원 상태 코드';

comment on column dwu_user.last_login_date is '마지막 로그인 일시';

comment on column dwu_user.login_fail_count is '로그인 실패 수';

comment on column dwu_user.kakao_id is '카카오 ID';

comment on column dwu_user.naver_id is '네이버 ID';
