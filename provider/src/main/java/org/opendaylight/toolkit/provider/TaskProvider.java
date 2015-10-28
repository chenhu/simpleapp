package org.opendaylight.toolkit.provider;

import java.util.List;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.EntryId;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.SaveEntryInput;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.Task;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.TaskService;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.saveentry.input.EntryField;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.task.Entry;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.task.EntryBuilder;
import org.opendaylight.yang.gen.v1.opendaylight.sample.rev140407.task.EntryKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a generated class based on the user inputs of application name
 * and fields json. This class implements a service created by yang model
 * @author harmansingh
 *
 */

public class TaskProvider implements TaskService, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(TaskProvider.class);

    private DataBroker dataService;

    public DataBroker getDataService() {
      return dataService;
    }

    public void setDataService(DataBroker dataService) {
      this.dataService = dataService;
    }

    public TaskProvider() {
    }

     /**
     * This is an example to show how yang based RPC can be used to perform an operation.
     * Here this is saving data in data store. This code also gives insight into how we can
     * insert data into data store
     * @param input
     * @return
     */

    @Override
    public Future<RpcResult<Void>> saveEntry(SaveEntryInput input) {
      log.debug("Saving the entry");
      if(input == null || input.getEntryId() == null) {
        log.debug("exiting the save entry because of invalid input");
        return null;
      }
      // EntryBuilder will be used to build an Entry object
      // We will store entry object in data store
      EntryBuilder entryBuilder = new EntryBuilder();
      entryBuilder.setKey(new EntryKey(new EntryId(input.getEntryId())));
      List<EntryField> entryFields = input.getEntryField();

      for(EntryField field : entryFields) {
        String key = field.getKey();
        String value = field.getValue();
        if(key == null || value == null) {
          continue;
        }
        switch(key) {
                    case "title" :
            entryBuilder.setTitle(value);
            break;
                    case "desc" :
            entryBuilder.setDesc(value);
            break;
                  }
      }
      final Entry entry = entryBuilder.build();
      final SettableFuture<RpcResult<Void>> futureResult = SettableFuture.create();
      addEntry(entry, futureResult);
      return futureResult;
    }

    private void addEntry(final Entry entry, final SettableFuture<RpcResult<Void>> futureResult) {
            // Each entry will be identifiable by a unique key, we have to create that identifier
            InstanceIdentifier.InstanceIdentifierBuilder<Entry> entryIdBuilder =
                    InstanceIdentifier.<Task>builder(Task.class)
                        .child(Entry.class, entry.getKey());
            InstanceIdentifier<Entry> path = entryIdBuilder.toInstance();
            // Place entry in data store tree
            WriteTransaction tx = dataService.newWriteOnlyTransaction();
            tx.put(LogicalDatastoreType.OPERATIONAL, path, entry, true);
            Futures.addCallback(tx.submit(), new FutureCallback<Void>() {
                @Override
                public void onSuccess(final Void result) {
                    futureResult.set(RpcResultBuilder.<Void>success().withResult(result).build());
                }

                @Override
                public void onFailure(final Throwable t) {
                    futureResult.set( RpcResultBuilder.<Void> failed()
                        .withError(RpcError.ErrorType.APPLICATION, t.getMessage() ).build() );
                }
            });

        }

    @Override
        public void close() throws Exception {
        }

}