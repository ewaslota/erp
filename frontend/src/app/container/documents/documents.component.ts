import { Component, OnInit } from '@angular/core';
import { CrudHeaderComponent } from './crud-header/crud-header.component';
import { CrudTableComponent } from './crud-table/crud-table.component';
import { CrudPaginationComponent } from './crud-pagination/crud-pagination.component';
import { DocumentEntity } from './document-entity.model';
import { DocumentService } from './document.service';
import { CrudFormComponent } from './crud-form/crud-form.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [
    CrudHeaderComponent,
    CrudTableComponent,
    CrudPaginationComponent,
    CrudFormComponent,
    NgIf,
  ],
  templateUrl: './documents.component.html',
  styleUrl: './documents.component.css',
})
export class DocumentsComponent implements OnInit {
  documentsList: DocumentEntity[] = [];
  pageNumber = 1;
  totalElements = 0;
  size = 25;
  totalPages = 0;
  sortColumns = 'id';
  sortDirections = 'asc';
  searchBy = '';

  showModal = false;
  modalMode: 'add' | 'edit' | 'view' | 'delete' = 'view';
  selectedDocument: DocumentEntity | null = null;
  errorMessage: string[] | null = null;

  constructor(private documentService: DocumentService) {}

  ngOnInit(): void {
    this.fetchDocuments(
      this.pageNumber,
      this.size,
      this.sortColumns,
      this.sortDirections,
      this.searchBy
    );
  }

  fetchDocuments(
    page: number,
    size: number,
    sortColumns: string,
    sortDirections: string,
    searchBy: string
  ): void {
    this.documentService
      .getDocuments(page - 1, size, sortColumns, sortDirections, searchBy)
      .subscribe((response) => {
        this.documentsList = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
      });
  }

  onPageChanged(page: number): void {
    this.pageNumber = page;
    this.fetchDocuments(
      this.pageNumber,
      this.size,
      this.sortColumns,
      this.sortDirections,
      this.searchBy
    );
  }

  onSortChanged(sortColumns: string, sortDirections: string): void {
    this.sortColumns = sortColumns;
    this.sortDirections = sortDirections;
    this.fetchDocuments(
      this.pageNumber,
      this.size,
      this.sortColumns,
      this.sortDirections,
      this.searchBy
    );
  }

  onSearchChanged(searchBy: string) {
    this.searchBy = searchBy;
    this.pageNumber = 1;
    this.fetchDocuments(
      this.pageNumber,
      this.size,
      this.sortColumns,
      this.sortDirections,
      this.searchBy
    );
  }

  openModal(
    mode: 'add' | 'edit' | 'view' | 'delete',
    document?: DocumentEntity
  ) {
    this.modalMode = mode;
    this.errorMessage = null; // Reset the error message when opening the modal
    if (mode === 'add') {
      this.selectedDocument = null; // Clear selected document for add mode
      this.showModal = true;
    } else if (document && document.id) {
      this.documentService.getDocument(document.id).subscribe(
        (documentData) => {
          this.selectedDocument = documentData;
          this.showModal = true;
        },
        (error) => {
          console.error('Error fetching document', error);
          // Handle error appropriately
        }
      );
    }
  }

  closeModal() {
    this.showModal = false;
  }

  saveDocument(document: DocumentEntity) {
    if (this.modalMode === 'add') {
      this.documentService.addDocument(document).subscribe(
        (newDocument) => {
          this.fetchDocuments(
            this.pageNumber,
            this.size,
            this.sortColumns,
            this.sortDirections,
            this.searchBy
          );
          this.closeModal();
        },
        (error) => {
          console.error('Error adding document', error);
          this.errorMessage = error;
          this.showModal = true; // Ensure the modal stays open
        }
      );
    } else if (this.modalMode === 'edit') {
      this.documentService.updateDocument(document).subscribe(
        (updatedDocument) => {
          const index = this.documentsList.findIndex(
            (e) => e.id === document.id
          );
          if (index !== -1) {
            this.documentsList[index] = updatedDocument; // Update the existing document in the list
          }
          this.closeModal();
        },
        (error) => {
          console.error('Error updating document', error);
          this.errorMessage = error;
          this.showModal = true; // Ensure the modal stays open
        }
      );
    }
  }

  deleteDocument(document: DocumentEntity) {
    this.documentService.deleteDocument(document.id).subscribe(
      () => {
        this.fetchDocuments(
          this.pageNumber,
          this.size,
          this.sortColumns,
          this.sortDirections,
          this.searchBy
        );
        this.closeModal();
      },
      (error) => {
        console.error('Error deleting document', error);
        this.errorMessage = error;
        this.showModal = true;
      }
    );
  }
}
