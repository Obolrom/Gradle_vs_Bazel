package com.romix.feature.feat240

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat240Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat240UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat240FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat240UserSummary
)

data class Feat240UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat240NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat240Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat240Config = Feat240Config()
) {

    fun loadSnapshot(userId: Long): Feat240NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat240NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat240UserSummary {
        return Feat240UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat240FeedItem> {
        val result = java.util.ArrayList<Feat240FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat240FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat240UiMapper {

    fun mapToUi(model: List<Feat240FeedItem>): Feat240UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat240UiModel(
            header = UiText("Feat240 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat240UiModel =
        Feat240UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat240UiModel =
        Feat240UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat240UiModel =
        Feat240UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat240Service(
    private val repository: Feat240Repository,
    private val uiMapper: Feat240UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat240UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat240UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat240UserItem1(val user: CoreUser, val label: String)
data class Feat240UserItem2(val user: CoreUser, val label: String)
data class Feat240UserItem3(val user: CoreUser, val label: String)
data class Feat240UserItem4(val user: CoreUser, val label: String)
data class Feat240UserItem5(val user: CoreUser, val label: String)
data class Feat240UserItem6(val user: CoreUser, val label: String)
data class Feat240UserItem7(val user: CoreUser, val label: String)
data class Feat240UserItem8(val user: CoreUser, val label: String)
data class Feat240UserItem9(val user: CoreUser, val label: String)
data class Feat240UserItem10(val user: CoreUser, val label: String)

data class Feat240StateBlock1(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock2(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock3(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock4(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock5(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock6(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock7(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock8(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock9(val state: Feat240UiModel, val checksum: Int)
data class Feat240StateBlock10(val state: Feat240UiModel, val checksum: Int)

fun buildFeat240UserItem(user: CoreUser, index: Int): Feat240UserItem1 {
    return Feat240UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat240StateBlock(model: Feat240UiModel): Feat240StateBlock1 {
    return Feat240StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat240UserSummary> {
    val list = java.util.ArrayList<Feat240UserSummary>(users.size)
    for (user in users) {
        list += Feat240UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat240UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat240UiModel {
    val summaries = (0 until count).map {
        Feat240UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat240UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat240UiModel> {
    val models = java.util.ArrayList<Feat240UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat240AnalyticsEvent1(val name: String, val value: String)
data class Feat240AnalyticsEvent2(val name: String, val value: String)
data class Feat240AnalyticsEvent3(val name: String, val value: String)
data class Feat240AnalyticsEvent4(val name: String, val value: String)
data class Feat240AnalyticsEvent5(val name: String, val value: String)
data class Feat240AnalyticsEvent6(val name: String, val value: String)
data class Feat240AnalyticsEvent7(val name: String, val value: String)
data class Feat240AnalyticsEvent8(val name: String, val value: String)
data class Feat240AnalyticsEvent9(val name: String, val value: String)
data class Feat240AnalyticsEvent10(val name: String, val value: String)

fun logFeat240Event1(event: Feat240AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat240Event2(event: Feat240AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat240Event3(event: Feat240AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat240Event4(event: Feat240AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat240Event5(event: Feat240AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat240Event6(event: Feat240AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat240Event7(event: Feat240AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat240Event8(event: Feat240AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat240Event9(event: Feat240AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat240Event10(event: Feat240AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat240Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat240Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat240(u: CoreUser): Feat240Projection1 =
    Feat240Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat240Projection1> {
    val list = java.util.ArrayList<Feat240Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat240(u)
    }
    return list
}
