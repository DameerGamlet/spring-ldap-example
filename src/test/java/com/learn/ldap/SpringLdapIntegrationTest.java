package com.learn.ldap;

import com.learn.ldap.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class SpringLdapIntegrationTest {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Test
    public void testGetAllPersons() {
        List<Person> persons = ldapTemplate.search(
                query().where("objectclass").is("person"), new PersonAttributesMapper());
        assertNotNull(persons);
        assertEquals(persons.size(), 3);
    }

    @Test
    public void testGetAllPersonsNames() {
        List<String> persons = ldapTemplate.search(
                query().where("objectclass").is("person"), new AttributesMapper<String>() {
                    public String mapFromAttributes(Attributes attrs)
                            throws NamingException {
                        return (String) attrs.get("cn").get();
                    }
                });
        assertNotNull(persons);
        assertEquals(persons.size(), 3);
    }

    @Test
    public void testFindPerson() {
        Person person = ldapTemplate.lookup("uid=john,ou=people,dc=memorynotfound,dc=com", new PersonAttributesMapper());
        assertNotNull(person);
        assertEquals(person.getFullName(), "John Doe");
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
