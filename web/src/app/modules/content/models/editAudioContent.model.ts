// TODO create data classes for recorded audio, file audio, and audio URL

export interface EditAudioContent {
    isPlaying: boolean;
    playAsync(): Promise<void>; 
    stopAsync(): Promise<void>;
}

export interface AudioContent {
    get type(): string;
    generateElement(): HTMLAudioElement;
    generateBlob(): Blob; 
}

export class BlobAudioContent implements AudioContent {
    constructor(private blob: Blob) {}

    get type(): string {
        return 'blob';
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = URL.createObjectURL(this.blob);
        return audioElement;
    }

    generateBlob(): Blob {
        return this.blob;
    }
}

export class FileAudioContent implements AudioContent {
    constructor(private file: File) {}

    get type(): string {
        return 'file';
    }

    get name(): string {
        return this.file.name;
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = URL.createObjectURL(this.file);
        return audioElement;
    }

    generateBlob(): Blob {
        return this.file;
    }

    static isInstanceOf(obj: any) {
        return obj?.type === 'file';
    }
}

