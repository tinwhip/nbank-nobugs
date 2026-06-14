package db.dao.impl;

import db.Databases;
import db.dao.CustomerDao;
import db.entity.CustomerEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CustomerDaoJdbc implements CustomerDao {

    @Override
    public Optional<CustomerEntity> findCustomerById(Long id) {
        try (Connection connection = Databases.connection()) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM customers WHERE id = ?"
            )) {
                ps.setLong(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        return Optional.of(
                                CustomerEntity.builder()
                                        .id(rs.getLong("id"))
                                        .username(rs.getString("username"))
                                        .password(rs.getString("password"))
                                        .name(rs.getString("name"))
                                        .role(rs.getString("role"))
                                        .build()
                        );
                    } else {
                        return Optional.empty();
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
