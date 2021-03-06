package org.opendaylight.toolkit.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.SaveEntryInputBuilder;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.TaskService;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.saveentry.input.EntryField;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.saveentry.input.EntryFieldBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a auto generated class which calls provider service to create an entry in
 * data store
 * @author harmansingh
 *
 */
public class TaskConsumerImpl implements TaskConsumerService {

  private static final Logger log = LoggerFactory.getLogger(TaskConsumerImpl.class);

  TaskService service;
  Random rand = new Random();

  public TaskConsumerImpl(TaskService service) {
    this.service = service;
  }

  public void createEntry(Map<String, String> data) {
    if(data == null || data.isEmpty()) {
      log.debug("empty input data");
      return;
    }
    List<EntryField> fields = new ArrayList<> ();
    for(String key : data.keySet()) {
      EntryFieldBuilder fieldBuilder = new EntryFieldBuilder();
      fieldBuilder.setKey(key);
      fieldBuilder.setValue(data.get(key));
      fields.add(fieldBuilder.build());
    }

    SaveEntryInputBuilder inputbuilder = new SaveEntryInputBuilder();
    inputbuilder.setEntryId(String.valueOf(rand.nextInt(1000000000)));
    inputbuilder.setEntryField(fields);
    try {
      RpcResult<Void> result = service.saveEntry(inputbuilder.build()).get();
      if(result.isSuccessful()) {
        log.debug("Successfully Added entry");
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

}
