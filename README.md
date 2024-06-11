# accent

## Accentuate the Negative

> What is `accent`?

`accent` is a tool to identify mispronunciations.

## Cost

> What's the cost?

While `accent` itself is free to use, it does depend on the Deepgram API and the OpenAI API, which might have their own charges.

- Deepgram: This app uses Deepgram's speech-to-text Nova 2 model. You get charged based on the total duration of speech processed. Since both your speech and the reference speech are evaluated, you can estimate the cost as twice the duration of your spoken input. For specific rates, check out [Deepgram Pricing](https://deepgram.com/pricing#:~:text=Nova%2D2-,%240.0043/min,-%240.0036/min).

- OpenAI: For converting text to speech, this app uses OpenAI's standard text-to-speech model. You get charged based on the amount of text converted to speech. For specific rates, check out [OpenAI Pricing](https://openai.com/api/pricing/#:~:text=TTS-,%2415.00%20/,1M%20characters,-TTS%20HD).

## Setup

> How do you set up `accent`?

1. Make sure you're using a Mac.

1. Install [devenv](https://github.com/cachix/devenv/blob/2837f4989338aaf03b5b4cf8bad91fe27150d984/docs/getting-started.md#installation).

1. Install [direnv](https://github.com/cachix/devenv/blob/2837f4989338aaf03b5b4cf8bad91fe27150d984/docs/automatic-shell-activation.md#installing-direnv).

1. Open a terminal window.

1. Run the following commands:

   ```sh
   git clone https://github.com/8ta4/accent
   cd accent
   direnv allow
   build
   mkdir -p ~/.config/accent
   ```

1. Open `~/.config/accent/config.yaml`.

1. Copy a Deepgram API key from [the Deepgram website](https://deepgram.com/).

1. Add the following field to the `config.yaml` file, replacing `your_deepgram_api_key` with your actual Deepgram API key:

   ```yaml
   deepgram: your_deepgram_api_key
   ```

1. Copy an OpenAI API key from [the OpenAI website](https://platform.openai.com/api-keys).

1. Add the following field to the `config.yaml` file, replacing `your_openai_api_key` with your actual OpenAI API key:

   ```yaml
   openai: your_openai_api_key
   ```

1. Save the `config.yaml` file.

## Usage

> How do I check how my pronunciation's doing?

1. Open a terminal on your computer.

1. Navigate to the directory where `accent` is installed.

1. Run the command `accent` to start the application.

1. If asked, allow `accent` to access your mic.

1. Start speaking.

1. Press `Space` when you want to evaluate your pronunciation up to that moment.

> How do I evaluate my pronunciation the second time?

After your initial evaluation, simply continue speaking and press `Space` whenever you want feedback on the new segment of speech you've just spoken. Each press of `Space` evaluates only the speech from the end of the last evaluation to the moment you press the key again.

> How can I clear any inputs that haven't been evaluated yet?

Press `Escape` to clear any inputs that haven't been evaluated yet.

If accessing `Escape` is inconvenient, consider remapping another key to work as `Escape`.
