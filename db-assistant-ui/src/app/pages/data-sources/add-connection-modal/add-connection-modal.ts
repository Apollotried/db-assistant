import {Component, EventEmitter, Output, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
declare var bootstrap: any;


interface DbConnection {
  name: string;
  dbType: string;
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  online: boolean;
}



@Component({
  selector: 'app-add-connection-modal',
  imports: [
    FormsModule
  ],
  templateUrl: './add-connection-modal.html',
  styleUrl: './add-connection-modal.scss'
})
export class AddConnectionModal {

  connection: DbConnection =  {
    name: '',
    dbType: 'postgresql',
    host: '',
    port: 5432,
    database: '',
    username: '',
    password: '',
    online: false,
  };

  @Output() add = new EventEmitter<any>();
  @Output() test = new EventEmitter<any>();

  submit() {
    this.add.emit(this.connection);
    this.close();
  }

  testConnection() {
    this.test.emit(this.connection);
  }

  close() {
    const modalEl = document.getElementById('addConnectionModal')!;
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal?.hide();
  }

  open() {
    const modalEl = document.getElementById('addConnectionModal')!;
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
  }


}
