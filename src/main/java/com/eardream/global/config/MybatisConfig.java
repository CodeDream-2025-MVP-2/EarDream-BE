package com.eardream.global.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 설정 클래스
 * TypeHandler 및 Alias 자동 등록
 */
@Configuration
public class MybatisConfig {

    /**
     * MyBatis Configuration Customizer - TypeHandler 등록
     */
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            TypeHandlerRegistry registry = configuration.getTypeHandlerRegistry();
            // String 타입에 대해 OracleNullTypeHandler 등록
            registry.register(String.class, new OracleNullTypeHandler());
        };
    }

    /**
     * Oracle DB의 CHAR(1) 'Y'/'N'을 Boolean으로 자동 변환하는 TypeHandler
     */
    @Bean
    public TypeHandler<Boolean> oracleBooleanTypeHandler() {
        return new org.apache.ibatis.type.TypeHandler<Boolean>() {
            @Override
            public void setParameter(java.sql.PreparedStatement ps, int i, Boolean parameter, 
                                   org.apache.ibatis.type.JdbcType jdbcType) throws java.sql.SQLException {
                if (parameter == null) {
                    ps.setString(i, "N");
                } else {
                    ps.setString(i, parameter ? "Y" : "N");
                }
            }

            @Override
            public Boolean getResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
                String value = rs.getString(columnName);
                return "Y".equals(value);
            }

            @Override
            public Boolean getResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
                String value = rs.getString(columnIndex);
                return "Y".equals(value);
            }

            @Override
            public Boolean getResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
                String value = cs.getString(columnIndex);
                return "Y".equals(value);
            }
        };
    }
}