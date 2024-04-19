package com.learn.ldap.repository;

import com.learn.ldap.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class PersonRepository {

    @Autowired
    private LdapTemplate ldapTemplate;

    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("person"),
                new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String) attrs.get("cn").get();
                    }
                });
    }

    public List<Person> getAllPersons() {
        return ldapTemplate.search(query()
                .where("objectclass").is("person"), new PersonAttributesMapper());
    }

    public Person findPerson(String dn) {
        return ldapTemplate.lookup(dn, new PersonAttributesMapper());
    }

    private class PersonAttributesMapper implements AttributesMapper<Person> {
        public Person mapFromAttributes(Attributes attrs) throws NamingException {
            Person person = new Person();
            person.setFullName((String)attrs.get("cn").get());
            person.setLastName((String)attrs.get("sn").get());
            return person;
        }
    }
}
