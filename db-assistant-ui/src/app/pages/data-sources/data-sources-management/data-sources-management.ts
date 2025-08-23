import { Component, ViewChild, signal  } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {AddConnectionModal} from '../add-connection-modal/add-connection-modal';
import {DatabaseConnectionsService} from '../../../services/services/database-connections.service';
import {DbConnectionResponse} from '../../../services/models/db-connection-response';
import {DbConnectionRequest} from '../../../services/models/db-connection-request';
import { ToastrService } from 'ngx-toastr';
import {TestConnectionModal} from '../test-connection-modal/test-connection-modal';
import {TestConnectionResponse} from '../../../services/models/test-connection-response';
import {Router} from '@angular/router';
import {Header} from '../../../components/header/header';



@Component({
  selector: 'app-data-sources-management',
  imports: [
    FormsModule,
    AddConnectionModal,
    TestConnectionModal,
    Header
  ],
  templateUrl: './data-sources-management.html',
  styleUrls: ['./data-sources-management.scss']
})
export class DataSourcesManagement {
  @ViewChild('addModal') addModal!: AddConnectionModal;
  @ViewChild('testModal') testModal!: TestConnectionModal;

  loading = signal(false);
  lastRefreshed = signal<Date | null>(null);

  totalConnections = signal(0);

  connections = signal<DbConnectionResponse[]>([]);
  activeConnection = signal<DbConnectionResponse | null>(null);

  connectionToTest: DbConnectionResponse | null = null;


  constructor(
    private dbService: DatabaseConnectionsService,
    private toastr: ToastrService,
    private router: Router
  ) {
  }


  loadConnections() {
    this.loading.set(true);

    this.dbService.listConnections().subscribe({
      next: (connections) => {
        this.connections.set(connections ?? []);
        this.totalConnections.set(connections.length);

        // Now get the active connection
        this.dbService.getActiveConnection().subscribe({
          next: (active) => {
            this.activeConnection.set(active);
            this.lastRefreshed.set(new Date());
            this.loading.set(false);
            this.toastr.success('Connections loaded successfully', 'Success', {
              timeOut: 4000,
              positionClass: 'toast-top-right'
            });

          },
          error: (err) => {
            console.log('Failed to get active connection: ' + (err.error ?? err.message ?? 'Unknown error'));
            this.toastr.error('Failed to load active connection', 'Error');
            this.loading.set(false);
          }
        });
      },
      error: (err) => {
        console.log('Failed to load connections: ' + (err.error ?? err.message ?? 'Unknown error'));
        this.toastr.error('Failed to load connections', 'Error');
        this.loading.set(false);
      }
    });
  }



openAddModal() {
    this.addModal.open();
  }



  onAddNew(connection: DbConnectionRequest) {
    this.loading.set(true);

    this.dbService.createAndActivateConnection({ body: connection }).subscribe({
      next: (active: DbConnectionResponse) =>{
        console.log('Connection created and activated successfully');
        this.toastr.success('Connection created and activated successfully', 'Success');
        this.activeConnection.set(active);
        this.loadConnections();
      },
      error: err => {
        console.log('Failed: ' + (err.error ?? err.message ?? 'Unknown error'));
        this.toastr.error('Failed to create connection', 'Error');
        this.loading.set(false);
      }
    });

  }

  onActivateConnection(connection: DbConnectionResponse){
    this.loading.set(true);
    if (!connection.id) {
      this.toastr.warning('Connection ID is missing', 'Warning');
      return;
    }

    this.dbService.activateConnection({ id: connection.id }).subscribe({
      next: (active: DbConnectionResponse) =>{
        this.toastr.success(`${active.name} activated successfully`, 'Success');
        this.activeConnection.set(active);
        this.loading.set(false);
      },
      error: err => {
        this.toastr.error('Failed to activate connection', 'Error');
        this.loading.set(false);
      }
    })

  }


  onTestConnection(connection: DbConnectionRequest) {

    this.loading.set(true);

    this.dbService.testConnection({ body: connection }).subscribe({
      next:(response) =>{
        console.log(response);

        if (response.status === 'success') {
          this.toastr.success(response.message, 'Success');
        } else {
          this.toastr.error(response.message, 'Error');
        }

        this.loading.set(false);
      },
      error: (err) =>{
        console.log('Failed: ' + (err.error ?? err.message ?? 'Unknown error'));
        this.toastr.error('Connection test failed', 'Error');
        this.loading.set(false);
      }
    });
  }

  onDeleteConnection(connection: DbConnectionResponse) {
    this.loading.set(true);
    if (!connection.id) {
      this.toastr.warning('Connection ID is missing', 'Warning');
      return;
    }

    this.dbService.deleteConnection({id: connection.id}).subscribe({
      next: () => {
        // Optimistically update UI before reloading
        this.connections.update(conns => conns.filter(c => c.id !== connection.id));
        this.totalConnections.update(total => total - 1);

        this.toastr.success('Connection deleted successfully', 'Success', {
          timeOut: 3000,
          progressBar: true
        });

        this.loading.set(false);

        // Optional: Reload for complete sync with backend
        this.loadConnections();
      },
      error: (err) => {
        console.log(err);
        this.toastr.error(`Failed to delete connection: ${err.message}`, 'Error', {
          timeOut: 5000,
          disableTimeOut: false
        });
        this.loading.set(false);
      }
    });
  }

  onOpenTestModal(connection: DbConnectionResponse) {
    this.connectionToTest = connection;
    this.testModal.open();
  }


  // In your DataSourcesManagement component
  onSubmitTestModal(password: string) {
    if (!this.connectionToTest) return;

    const request: DbConnectionRequest = {
      ...this.connectionToTest,
      password
    };

    this.loading.set(true);
    this.dbService.testConnection({ body: request }).subscribe({
      next: (response: TestConnectionResponse) => {
        this.testModal.onTestComplete(true, response.message as string);
        this.loading.set(false);
      },
      error: (err) => {
        const errorMsg =  'Connection test failed';
        this.testModal.onTestComplete(false, errorMsg);
        this.loading.set(false);
      }
    });
  }


  goToLLM() {
    this.router.navigate(['/llm']);
  }




  mapToRequest(conn: DbConnectionResponse): DbConnectionRequest {
    return {
      name: conn.name,
      dbType: conn.dbType,
      host: conn.host,
      port: conn.port,
      database: conn.database,
      username: conn.username,
      password: ''
    };
  }

}
