import {Component, EventEmitter, Output, Input} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
declare var bootstrap: any;

@Component({
  selector: 'app-test-connection-modal',
  imports: [
    FormsModule
  ],
  templateUrl: './test-connection-modal.html',
  styleUrl: './test-connection-modal.scss'
})
export class TestConnectionModal {
  @Input() connectionName!: string;
  @Output() submitPassword = new EventEmitter<string>();
  @Output() closed = new EventEmitter<void>();
  password: string = '';
  showPassword = false;
  isOpen = false;
  testing = false;

  constructor(
    private toastr: ToastrService
  ) {
  }


  confirm() {
    if (!this.password) {
      this.toastr.warning('Please enter a password', 'Warning');
      return;
    }

    this.testing = true;
    this.submitPassword.emit(this.password);
  }

  open() {
    this.isOpen = true;
    this.password = '';
  }

  close() {
    this.password = '';
    this.isOpen = false;
    this.closed.emit();
  }


  onTestComplete(success: boolean, message: string) {
    this.testing = false;
    if (success) {
      this.toastr.success(message, 'Connection Successful');
    } else {
      this.toastr.error(message, 'Connection Failed');
    }
    this.close();
  }



  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
    const input = document.getElementById('connectionPassword') as HTMLInputElement;
    input.type = this.showPassword ? 'text' : 'password';
  }

  }
