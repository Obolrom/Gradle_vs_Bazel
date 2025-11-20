package com.romix.feature.feat112

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat112Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat112UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat112FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat112UserSummary
)

data class Feat112UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat112NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat112Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat112Config = Feat112Config()
) {

    fun loadSnapshot(userId: Long): Feat112NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat112NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat112UserSummary {
        return Feat112UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat112FeedItem> {
        val result = java.util.ArrayList<Feat112FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat112FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat112UiMapper {

    fun mapToUi(model: List<Feat112FeedItem>): Feat112UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat112UiModel(
            header = UiText("Feat112 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat112UiModel =
        Feat112UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat112UiModel =
        Feat112UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat112UiModel =
        Feat112UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat112Service(
    private val repository: Feat112Repository,
    private val uiMapper: Feat112UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat112UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat112UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat112UserItem1(val user: CoreUser, val label: String)
data class Feat112UserItem2(val user: CoreUser, val label: String)
data class Feat112UserItem3(val user: CoreUser, val label: String)
data class Feat112UserItem4(val user: CoreUser, val label: String)
data class Feat112UserItem5(val user: CoreUser, val label: String)
data class Feat112UserItem6(val user: CoreUser, val label: String)
data class Feat112UserItem7(val user: CoreUser, val label: String)
data class Feat112UserItem8(val user: CoreUser, val label: String)
data class Feat112UserItem9(val user: CoreUser, val label: String)
data class Feat112UserItem10(val user: CoreUser, val label: String)

data class Feat112StateBlock1(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock2(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock3(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock4(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock5(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock6(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock7(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock8(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock9(val state: Feat112UiModel, val checksum: Int)
data class Feat112StateBlock10(val state: Feat112UiModel, val checksum: Int)

fun buildFeat112UserItem(user: CoreUser, index: Int): Feat112UserItem1 {
    return Feat112UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat112StateBlock(model: Feat112UiModel): Feat112StateBlock1 {
    return Feat112StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat112UserSummary> {
    val list = java.util.ArrayList<Feat112UserSummary>(users.size)
    for (user in users) {
        list += Feat112UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat112UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat112UiModel {
    val summaries = (0 until count).map {
        Feat112UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat112UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat112UiModel> {
    val models = java.util.ArrayList<Feat112UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat112AnalyticsEvent1(val name: String, val value: String)
data class Feat112AnalyticsEvent2(val name: String, val value: String)
data class Feat112AnalyticsEvent3(val name: String, val value: String)
data class Feat112AnalyticsEvent4(val name: String, val value: String)
data class Feat112AnalyticsEvent5(val name: String, val value: String)
data class Feat112AnalyticsEvent6(val name: String, val value: String)
data class Feat112AnalyticsEvent7(val name: String, val value: String)
data class Feat112AnalyticsEvent8(val name: String, val value: String)
data class Feat112AnalyticsEvent9(val name: String, val value: String)
data class Feat112AnalyticsEvent10(val name: String, val value: String)

fun logFeat112Event1(event: Feat112AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat112Event2(event: Feat112AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat112Event3(event: Feat112AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat112Event4(event: Feat112AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat112Event5(event: Feat112AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat112Event6(event: Feat112AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat112Event7(event: Feat112AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat112Event8(event: Feat112AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat112Event9(event: Feat112AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat112Event10(event: Feat112AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat112Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat112Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat112(u: CoreUser): Feat112Projection1 =
    Feat112Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat112Projection1> {
    val list = java.util.ArrayList<Feat112Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat112(u)
    }
    return list
}
