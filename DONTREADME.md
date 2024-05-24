# accent

## Goals

### Accuracy

> Does `accent` measure if I'm pronouncing things like a native speaker?

Nope, `accent` isn't about matching a standard. Here’s why:

- Machine Understandability: As voice tech gets more common, it's important that machines get what you're saying. Accent aims to improve how well speech recognition systems understand you, not just how "correct" your accent sounds.

- Variability: Pronunciation standards vary widely even among native speakers. Languages and accents evolve. Keeping a up-to-date list of all these variations is a huge task.

> Is `accent` 100% accurate with its pronunciation checks?

Yes, within the context of how `accent` defines accuracy. `accent` is all about enhancing how well machines understand you. It uses speech recognition technology to evaluate your speech. This is the gold standard, and by that measure, `accent` nails it.

### Latency

> What is the latency goal?

The goal is to keep response times within 1 second.

> Why a 1-second latency goal?

1 second is pretty much the limit for keeping your flow of thought uninterrupted. Quick feedback is crucial to keep you focused. That's why the interface is tweaked to make things feel faster.

### Cost

> What is the cost goal?

I want to keep the cost under $100 a month.

This decision is influenced by comparisons with:

- Personal Pronunciation Coaching: Personal pronunciation coaching can go for about $1,000 a month. `accent` offers a lot at a fraction of that price, making it a budget choice.

- Other Productivity Tools: Lots of the top tools that help you out professionally are priced under $100 a month.

> Does `accent` make personal pronunciation coaching obsolete?

No. Here's a list of benefits that personal pronunciation coaches offer that the `accent` tool doesn't currently provide:

- Accent Influence Analysis: Coaches can spot how your usual accent messes with the new one you're learning.

- Phonetic Explanations: Coaches can delve into phonetic phenomena that might be affecting your pronunciation.

- Recurring Issue Identification: Coaches are great at spotting recurring issues with specific sounds.

- Study Plans: Coaches can set you up with study plans tailored just for you.

- Motivational Support: Coaches do that keep you motivated, which is important for sticking with your practice.

## Functionality

### Evaluation

> Can the pronunciation score take a positive value?

Yes. The score in the accent tool can totally be positive. Normally, the score is the difference between two probabilities. Since both probabilities are between 0 and 1, the difference will range from -1 to 1.

> What probabilities may be used to calculate the pronunciation score?

The score is calculated using probability that may include:

- User's Speech Probability: This is the probability assigned by the speech-to-text API to each word it successfully transcribes from your speech. This probability reflects the system's confidence in its transcription of what you said.

- Reference Speech Probability: This is the probability assigned by the speech-to-text API to each word it successfully transcribes from the reference speech. This probability serves as a benchmark for ideal pronunciation.

But don't worry, you don't have to speak twice, as the saying goes, "measure twice, speak once."

> Does each word in the user's speech have a corresponding word in the reference speech?

Nope, it's not a sure thing. The whole process of turning text to speech and then back to text isn't an involution. Ideally, converting text to speech and then transcribing it back to text should return the original text. But in reality, this back-and-forth can mess things up because the tech isn't flawless.

> What is the score when a word in the user's speech has a corresponding word in the reference speech?

When there is a corresponding word in the reference speech, here's how I figure out your score:

$$\text{Score} = P_{\text{user}} - P_{\text{reference}}$$

Here, $P_{\text{user}}$ is the probability of your word. That's how sure the system is that it heard you right. And $P_{\text{reference}}$ is the probability of the corresponding word in the reference speech.

> What is the score when a word in the user's speech does not have a corresponding word in the reference speech?

When there is no corresponding word in the reference speech for a word you said, the score is:

$$\text{Score} = P_{\text{user}} - 1$$

Here, $P_{\text{user}}$ is the probability of your word.

This means I take your word's probability and subtract it from 1, which is the perfect score, assuming the reference would have been spot on.

> Why not use a ratio of probabilities to calculate the pronunciation score?

Using a ratio of probabilities sounds neat until you hit a snag where both probabilities are 0. That gives you something like $\frac{0}{0}$, and math doesn't like that. It's undefined.

### Speech to Text

> How many speech-to-text API calls are needed?

2

Here's how it works:

1. The system uses a speech-to-text API to transcribe your voice and get a transcript with probabilities for each word.

1. The system uses a text-to-speech API to generate voice from that transcript.

1. The system uses the same speech-to-text API to transcribe the generated voice and get another transcript with probabilities for each word.

> Does this tool use macOS's built-in speech to text?

No, it doesn't. The built-in speech to text on macOS doesn't provide word-level probabilities.

> Does this tool use the Whisper API provided by OpenAI?

No. The open-source version of Whisper might be able to give you word-level probabilities if you use a specific flag, but the API that OpenAI provides doesn't seem to have this feature.

> Is the choice of speech-to-text API based on the lowest overall word error rate?

Nope. Instead of focusing on that, the tool uses an API that has a low word error rate:

- for high-frequency words since pronunciation practice is mainly about these and can steer clear of uncommon words that speech-to-text APIs often trip up on.

- in low-noise settings because it's fair to tell users to practice somewhere quiet.

> What speech-to-text API does this tool use?

`accent` uses Deepgram's speech-to-text API. But Deepgram doesn't support custom cross-origin resource sharing (CORS). According to their documentation, "[This SDK now works in the browser. If you'd like to make REST-based requests (pre-recorded transcription, on-premise, and management requests), then you'll need to use a proxy as we do not support custom CORS origins](https://github.com/deepgram/deepgram-js-sdk/blob/af5b1a5b1218a86a9ff140a0a324c4962a1e6ca7/README.md?plain=1#L148)."

> Does this tool use a proxy?

No, `accent` doesn't use a proxy. `accent` is an Electron app. That means `accent` can use Node.js to make API requests without dealing with CORS issues.

> Is this tool built on a streaming API transcription?

Nah, it isn't. Streaming APIs tend to trade off accuracy because they don't take into account the context that comes later.

### Text to Speech

> How many text-to-speech API calls are needed?

1

> Does this tool use Deepgram's text-to-speech API?

No, it doesn't. Deepgram's text to speech sometimes mispronounces words.

> Does this tool use macOS's built-in text to speech?

No, it doesn't. The built-in text to speech on macOS sounds low-quality.

> Does this tool use ElevenLabs' text to speech?

Nope, because it's too pricey. [For 40 hours of audio a month, it would cost $330](https://elevenlabs.io/pricing#:~:text=Scale-,%24330/mo,-For%20growing%20publishers), and that's over budget.

> What text-to-speech API does this tool use?

`accent` uses OpenAI's text-to-speech API.

### Recording

> Does this tool store any audio at all?

Yep, `accent` does temporarily store audio, but it uses the Opus format.

> Why Opus and not MP3?

Opus is pretty cool because it allows real-time compression. MP3 needs the whole audio file to get the best encoding.

Plus, Opus gives you a small file size but still keeps the quality high.

For MP3, it's recommended to use 128 kbps for [audiobooks](<https://support.google.com/books/partner/answer/7504302#file-formats:~:text=mp3%20(cbr%20preferred)%2C%20%3E%3D128kbps%20(mono)>)/[podcasts](https://learn.acast.com/en/articles/3505536-which-audio-file-format-should-i-use-for-my-podcast#sharing:~:text=we%20recommend%20uploading%20mp3%20files%20with%20a%20bitrate%20of%20128kbps.). But if you're using Opus, you can get away with just 24 Kbps for [audiobooks/podcasts](https://wiki.xiph.org/Opus_Recommended_Settings#Recommended_Bitrates:~:text=down%20this%20page.-,Audiobooks%20%2F%20Podcasts,24,-Bitrates%20from%20here).

> How many times larger is a 128 kbps MP3 file than a 24 kbps Opus file?

It's about 5.3333 times larger.

$$\frac{128\text{ kbps}}{24\text{ kbps}} \approx 5.3333$$

> Is latency linearly proportional to file size?

No, latency does not increase linearly with file size. TCP slow start gradually increases data transmission rate. So, even though MP3 file is larger, the actual latency does not increase proportionally.

> What sample rate is used?

`say` uses a sample rate of 16 kHz. Google recommends [a sample rate of at least 16 kHz in the audio files that you use for transcription](https://cloud.google.com/speech-to-text/docs/optimizing-audio-files-for-speech-to-text#sample_rate_frequency_range:~:text=We%20recommend%20a%20sample%20rate%20of%20at%20least%2016%20kHz%20in%20the%20audio%20files%20that%20you%20use%20for%20transcription%20with%20Speech%2Dto%2DText.).
