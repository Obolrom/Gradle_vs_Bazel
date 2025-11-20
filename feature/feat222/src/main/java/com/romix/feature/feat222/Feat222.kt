package com.romix.feature.feat222

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat222Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat222UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat222FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat222UserSummary
)

data class Feat222UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat222NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat222Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat222Config = Feat222Config()
) {

    fun loadSnapshot(userId: Long): Feat222NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat222NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat222UserSummary {
        return Feat222UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat222FeedItem> {
        val result = java.util.ArrayList<Feat222FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat222FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat222UiMapper {

    fun mapToUi(model: List<Feat222FeedItem>): Feat222UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat222UiModel(
            header = UiText("Feat222 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat222UiModel =
        Feat222UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat222UiModel =
        Feat222UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat222UiModel =
        Feat222UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat222Service(
    private val repository: Feat222Repository,
    private val uiMapper: Feat222UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat222UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat222UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat222UserItem1(val user: CoreUser, val label: String)
data class Feat222UserItem2(val user: CoreUser, val label: String)
data class Feat222UserItem3(val user: CoreUser, val label: String)
data class Feat222UserItem4(val user: CoreUser, val label: String)
data class Feat222UserItem5(val user: CoreUser, val label: String)
data class Feat222UserItem6(val user: CoreUser, val label: String)
data class Feat222UserItem7(val user: CoreUser, val label: String)
data class Feat222UserItem8(val user: CoreUser, val label: String)
data class Feat222UserItem9(val user: CoreUser, val label: String)
data class Feat222UserItem10(val user: CoreUser, val label: String)

data class Feat222StateBlock1(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock2(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock3(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock4(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock5(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock6(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock7(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock8(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock9(val state: Feat222UiModel, val checksum: Int)
data class Feat222StateBlock10(val state: Feat222UiModel, val checksum: Int)

fun buildFeat222UserItem(user: CoreUser, index: Int): Feat222UserItem1 {
    return Feat222UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat222StateBlock(model: Feat222UiModel): Feat222StateBlock1 {
    return Feat222StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat222UserSummary> {
    val list = java.util.ArrayList<Feat222UserSummary>(users.size)
    for (user in users) {
        list += Feat222UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat222UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat222UiModel {
    val summaries = (0 until count).map {
        Feat222UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat222UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat222UiModel> {
    val models = java.util.ArrayList<Feat222UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat222AnalyticsEvent1(val name: String, val value: String)
data class Feat222AnalyticsEvent2(val name: String, val value: String)
data class Feat222AnalyticsEvent3(val name: String, val value: String)
data class Feat222AnalyticsEvent4(val name: String, val value: String)
data class Feat222AnalyticsEvent5(val name: String, val value: String)
data class Feat222AnalyticsEvent6(val name: String, val value: String)
data class Feat222AnalyticsEvent7(val name: String, val value: String)
data class Feat222AnalyticsEvent8(val name: String, val value: String)
data class Feat222AnalyticsEvent9(val name: String, val value: String)
data class Feat222AnalyticsEvent10(val name: String, val value: String)

fun logFeat222Event1(event: Feat222AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat222Event2(event: Feat222AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat222Event3(event: Feat222AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat222Event4(event: Feat222AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat222Event5(event: Feat222AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat222Event6(event: Feat222AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat222Event7(event: Feat222AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat222Event8(event: Feat222AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat222Event9(event: Feat222AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat222Event10(event: Feat222AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat222Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat222Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat222(u: CoreUser): Feat222Projection1 =
    Feat222Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat222Projection1> {
    val list = java.util.ArrayList<Feat222Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat222(u)
    }
    return list
}
