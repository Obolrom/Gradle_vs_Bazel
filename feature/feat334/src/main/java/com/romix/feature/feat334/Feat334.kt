package com.romix.feature.feat334

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat334Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat334UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat334FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat334UserSummary
)

data class Feat334UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat334NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat334Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat334Config = Feat334Config()
) {

    fun loadSnapshot(userId: Long): Feat334NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat334NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat334UserSummary {
        return Feat334UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat334FeedItem> {
        val result = java.util.ArrayList<Feat334FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat334FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat334UiMapper {

    fun mapToUi(model: List<Feat334FeedItem>): Feat334UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat334UiModel(
            header = UiText("Feat334 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat334UiModel =
        Feat334UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat334UiModel =
        Feat334UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat334UiModel =
        Feat334UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat334Service(
    private val repository: Feat334Repository,
    private val uiMapper: Feat334UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat334UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat334UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat334UserItem1(val user: CoreUser, val label: String)
data class Feat334UserItem2(val user: CoreUser, val label: String)
data class Feat334UserItem3(val user: CoreUser, val label: String)
data class Feat334UserItem4(val user: CoreUser, val label: String)
data class Feat334UserItem5(val user: CoreUser, val label: String)
data class Feat334UserItem6(val user: CoreUser, val label: String)
data class Feat334UserItem7(val user: CoreUser, val label: String)
data class Feat334UserItem8(val user: CoreUser, val label: String)
data class Feat334UserItem9(val user: CoreUser, val label: String)
data class Feat334UserItem10(val user: CoreUser, val label: String)

data class Feat334StateBlock1(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock2(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock3(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock4(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock5(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock6(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock7(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock8(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock9(val state: Feat334UiModel, val checksum: Int)
data class Feat334StateBlock10(val state: Feat334UiModel, val checksum: Int)

fun buildFeat334UserItem(user: CoreUser, index: Int): Feat334UserItem1 {
    return Feat334UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat334StateBlock(model: Feat334UiModel): Feat334StateBlock1 {
    return Feat334StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat334UserSummary> {
    val list = java.util.ArrayList<Feat334UserSummary>(users.size)
    for (user in users) {
        list += Feat334UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat334UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat334UiModel {
    val summaries = (0 until count).map {
        Feat334UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat334UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat334UiModel> {
    val models = java.util.ArrayList<Feat334UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat334AnalyticsEvent1(val name: String, val value: String)
data class Feat334AnalyticsEvent2(val name: String, val value: String)
data class Feat334AnalyticsEvent3(val name: String, val value: String)
data class Feat334AnalyticsEvent4(val name: String, val value: String)
data class Feat334AnalyticsEvent5(val name: String, val value: String)
data class Feat334AnalyticsEvent6(val name: String, val value: String)
data class Feat334AnalyticsEvent7(val name: String, val value: String)
data class Feat334AnalyticsEvent8(val name: String, val value: String)
data class Feat334AnalyticsEvent9(val name: String, val value: String)
data class Feat334AnalyticsEvent10(val name: String, val value: String)

fun logFeat334Event1(event: Feat334AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat334Event2(event: Feat334AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat334Event3(event: Feat334AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat334Event4(event: Feat334AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat334Event5(event: Feat334AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat334Event6(event: Feat334AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat334Event7(event: Feat334AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat334Event8(event: Feat334AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat334Event9(event: Feat334AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat334Event10(event: Feat334AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat334Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat334Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat334(u: CoreUser): Feat334Projection1 =
    Feat334Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat334Projection1> {
    val list = java.util.ArrayList<Feat334Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat334(u)
    }
    return list
}
