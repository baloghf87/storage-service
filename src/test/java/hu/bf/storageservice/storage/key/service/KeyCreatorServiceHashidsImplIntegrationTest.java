package hu.bf.storageservice.storage.key.service;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {KeyCreatorServiceHashidsImpl.class})
public class KeyCreatorServiceHashidsImplIntegrationTest extends KeyCreatorServiceIntegrationTest {

}