import { Component, inject, OnInit } from '@angular/core';
import { LocationNodeService } from '../../shared/services/location-node-service';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card'; // <-- import MatCardModule

@Component({
  selector: 'app-location-node-table-component',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatDialogModule, CommonModule, MatCardModule],
  templateUrl: './locationnode-table-component.html',
  styleUrls: ['./locationnode-table-component.css']
})
export class LocationNodeTableComponent {
  private nodeService = inject(LocationNodeService);
  nodes: any[] = [];
    private snackBar = inject(MatSnackBar);


  ngOnInit() {
    this.loadNodes();
  }

  loadNodes() {
    this.nodeService.getAllNodes().subscribe({
      next: (data) => this.nodes = data,
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to load nodes', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }


    deleteNode(nodeId: number) {
    this.nodeService.deleteNode(nodeId).subscribe({
      next: () => {
        this.snackBar.open('Node deleted successfully', 'Close', { duration: 3000, panelClass: ['snackbar-success'] });
        this.nodes = this.nodes.filter(t => t.id !== nodeId); // remove from local array
      },
      error: (err) => {
        console.error(err);
        this.snackBar.open('Failed to delete node', 'Close', { duration: 3000, panelClass: ['snackbar-error'] });
      }
    });
  }
}
