import {Component, OnInit} from '@angular/core';
import {QueryHistoryResponseDto} from '../../../services/models/query-history-response-dto';
import {NgClass} from '@angular/common';
import {QueryHistoryService} from '../../../services/services/query-history.service';
import {Header} from '../../../components/header/header';

@Component({
  selector: 'app-query-history',
  imports: [
    NgClass,
    Header
  ],
  templateUrl: './query-history.html',
  styleUrl: './query-history.scss'
})
export class QueryHistory implements OnInit{
  queryHistory: QueryHistoryResponseDto[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(
    private queryService: QueryHistoryService
  ) {
  }

  ngOnInit() {
    this.loadQueryHistory();
  }

  loadQueryHistory() {
    this.isLoading = true;
    this.error = null;

    this.queryService.queryHistory().subscribe({
      next: (history) => {
        console.log(history);
        this.queryHistory = history;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = 'Failed to load query history';
        this.isLoading = false;
      }
    })
  }

  formatDate(date: string | undefined): string {
    if(!date){
      return 'N/A';
    }
    return new Date(date).toLocaleString();
  }

  getQueryTypeClass(queryType: string | undefined): string {
    switch (queryType) {
      case 'SELECT': return 'badge bg-primary';
      case 'INSERT': return 'badge bg-success';
      case 'UPDATE': return 'badge bg-warning';
      case 'DELETE': return 'badge bg-danger';
      default: return 'badge bg-secondary';
    }
  }

  copyQueryToClipboard(query: string | undefined) {
    if(!query){
      return;
    }
    navigator.clipboard.writeText(query).then(() => {
      // Optional: Show toast notification
    });
  }

}
