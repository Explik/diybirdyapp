import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AudioUploadService {
  private uploadUrl = environment.apiUrl + "/file/upload";

  constructor(private http: HttpClient) {}

  uploadAudio(audioBlob: Blob): Observable<FileUploadResultDto> {
    const formData = new FormData();
    formData.append('file', audioBlob, 'recording.webm');
    return this.http.post(this.uploadUrl, formData).pipe(map((response: any) => response as FileUploadResultDto));
  }
}
