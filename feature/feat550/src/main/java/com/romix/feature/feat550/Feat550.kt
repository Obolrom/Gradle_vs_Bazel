package com.romix.feature.feat550

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat550Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat550UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat550FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat550UserSummary
)

data class Feat550UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat550NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat550Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat550Config = Feat550Config()
) {

    fun loadSnapshot(userId: Long): Feat550NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat550NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat550UserSummary {
        return Feat550UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat550FeedItem> {
        val result = java.util.ArrayList<Feat550FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat550FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat550UiMapper {

    fun mapToUi(model: List<Feat550FeedItem>): Feat550UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat550UiModel(
            header = UiText("Feat550 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat550UiModel =
        Feat550UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat550UiModel =
        Feat550UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat550UiModel =
        Feat550UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat550Service(
    private val repository: Feat550Repository,
    private val uiMapper: Feat550UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat550UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat550UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat550UserItem1(val user: CoreUser, val label: String)
data class Feat550UserItem2(val user: CoreUser, val label: String)
data class Feat550UserItem3(val user: CoreUser, val label: String)
data class Feat550UserItem4(val user: CoreUser, val label: String)
data class Feat550UserItem5(val user: CoreUser, val label: String)
data class Feat550UserItem6(val user: CoreUser, val label: String)
data class Feat550UserItem7(val user: CoreUser, val label: String)
data class Feat550UserItem8(val user: CoreUser, val label: String)
data class Feat550UserItem9(val user: CoreUser, val label: String)
data class Feat550UserItem10(val user: CoreUser, val label: String)

data class Feat550StateBlock1(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock2(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock3(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock4(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock5(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock6(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock7(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock8(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock9(val state: Feat550UiModel, val checksum: Int)
data class Feat550StateBlock10(val state: Feat550UiModel, val checksum: Int)

fun buildFeat550UserItem(user: CoreUser, index: Int): Feat550UserItem1 {
    return Feat550UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat550StateBlock(model: Feat550UiModel): Feat550StateBlock1 {
    return Feat550StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat550UserSummary> {
    val list = java.util.ArrayList<Feat550UserSummary>(users.size)
    for (user in users) {
        list += Feat550UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat550UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat550UiModel {
    val summaries = (0 until count).map {
        Feat550UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat550UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat550UiModel> {
    val models = java.util.ArrayList<Feat550UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat550AnalyticsEvent1(val name: String, val value: String)
data class Feat550AnalyticsEvent2(val name: String, val value: String)
data class Feat550AnalyticsEvent3(val name: String, val value: String)
data class Feat550AnalyticsEvent4(val name: String, val value: String)
data class Feat550AnalyticsEvent5(val name: String, val value: String)
data class Feat550AnalyticsEvent6(val name: String, val value: String)
data class Feat550AnalyticsEvent7(val name: String, val value: String)
data class Feat550AnalyticsEvent8(val name: String, val value: String)
data class Feat550AnalyticsEvent9(val name: String, val value: String)
data class Feat550AnalyticsEvent10(val name: String, val value: String)

fun logFeat550Event1(event: Feat550AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat550Event2(event: Feat550AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat550Event3(event: Feat550AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat550Event4(event: Feat550AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat550Event5(event: Feat550AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat550Event6(event: Feat550AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat550Event7(event: Feat550AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat550Event8(event: Feat550AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat550Event9(event: Feat550AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat550Event10(event: Feat550AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat550Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat550Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat550(u: CoreUser): Feat550Projection1 =
    Feat550Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat550Projection1> {
    val list = java.util.ArrayList<Feat550Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat550(u)
    }
    return list
}
